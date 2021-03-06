package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.message.MqSender;
import com.highpowerbear.hpbtrader.mktdata.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.ib.client.Contract;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@ApplicationScoped
public class HistDataController {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private IbController ibController;
    @Inject private MqSender mqSender;
    @Inject private WebsocketController websocketController;

    private DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private Map<Integer, Map<Long, DataBar>> barsReceivedMap = new ConcurrentHashMap<>(); // reqId --> (timeInMillisBarClose --> bar)

    public void barReceived(int reqId, String date, DataBar dataBar) {
        int seriesId = reqId / HtrDefinitions.IB_REQUEST_MULT;
        DataSeries dataSeries = dataSeriesDao.findDataSeries(seriesId);
        barsReceivedMap.putIfAbsent(reqId, new LinkedHashMap<>());
        Calendar c = HtrUtil.getCalendar();
        c.setTimeInMillis(Long.valueOf(date) * 1000 + dataSeries.getBarType().getMillis()); // date-time stamp of the end of the bar
        if (HtrEnums.BarType.MIN_60.equals(dataSeries.getBarType())) {
            c.set(Calendar.MINUTE, 0); // needed in case of bars started at 9:30 (END 10:00 not 10:30) or 17:15 (END 18:00 not 18:15)
        }
        dataBar.setBarCloseDate(c);
        dataBar.setDataSeries(dataSeries);
        barsReceivedMap.get(reqId).put(dataBar.getBarCloseDateMillis(), dataBar);
    }

    public void reqFinished(int reqId) {
        int seriesId = reqId / HtrDefinitions.IB_REQUEST_MULT;
        DataSeries dataSeries = dataSeriesDao.findDataSeries(seriesId);
        // remove last bar if it is not finished yet
        new LinkedHashSet<>(barsReceivedMap.get(reqId).keySet())
                .stream()
                .filter(timeInMillisBarClose -> timeInMillisBarClose > System.currentTimeMillis())
                .forEach(barsReceivedMap.get(reqId)::remove);
        List<DataBar> bars = new ArrayList<>(barsReceivedMap.get(reqId).values());
        dataSeriesDao.createOrUpdateDataBars(dataSeries, bars);
        websocketController.notifyDataBarsCreated(dataSeries);
        DataBar lastDataBar = bars.get(bars.size() - 1);
        boolean isCurrentLastBar = ((lastDataBar.getBarCloseDateMillis() + dataSeries.getBarType().getMillis()) > System.currentTimeMillis());
        if (isCurrentLastBar) {
            mqSender.notifyDataBarsCreated(dataSeries);
        }
        barsReceivedMap.remove(reqId);
    }

    void requestFiveMinBars() {
        l.info("START requestFiveMinBars");
        dataSeriesDao.getDataSeriesByBarType(HtrEnums.BarType.MIN_5).stream().filter(DataSeries::isActive).forEach(s -> {
            Contract contract = s.getInstrument().createContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getInstrument().getSecType()) ? HtrDefinitions.IB_ETH_TOO : HtrDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrDefinitions.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    (HtrEnums.SecType.CASH.equals(s.getInstrument().getSecType()) ? HtrDefinitions.IB_DURATION_1_DAY : HtrDefinitions.IB_DURATION_2_DAY),
                    HtrDefinitions.IB_BAR_5_MIN,
                    s.getInstrument().getSecType().getIbWhatToShow(),
                    isUseRTH,
                    HtrDefinitions.IB_FORMAT_DATE_MILLIS);
        });
        l.info("END requestFiveMinBars");
    }

    void requestSixtyMinBars() {
        l.info("START requestSixtyMinBars");
        dataSeriesDao.getDataSeriesByBarType(HtrEnums.BarType.MIN_60).stream().filter(DataSeries::isActive).forEach(s -> {
            Contract contract = s.getInstrument().createContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getInstrument().getSecType()) ? HtrDefinitions.IB_ETH_TOO : HtrDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrDefinitions.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    HtrDefinitions.IB_DURATION_1_WEEK,
                    HtrDefinitions.IB_BAR_1_HOUR,
                    s.getInstrument().getSecType().getIbWhatToShow(),
                    isUseRTH,
                    HtrDefinitions.IB_FORMAT_DATE_MILLIS);
        });
        l.info("END requestSixtyMinBars");
    }

    public void backfill(DataSeries dataSeries) {
        if (!dataSeries.isActive()) {
            l.info("Series not active, backfill won't be performed, seriesId=" + dataSeries.getId() + ", symbol=" + dataSeries.getInstrument().getSymbol());
            return;
        }
        l.info("START backfillManual, series=" + dataSeries.getId() + ", symbol=" + dataSeries.getInstrument().getSymbol());
        if (!ibController.isAnyActiveMktDataConnection()) {
            return;
        }
        Contract contract = dataSeries.getInstrument().createContract();
        Calendar now = HtrUtil.getCalendar();
        int isUseRTH = (HtrEnums.SecType.FUT.equals(dataSeries.getInstrument().getSecType()) ? HtrDefinitions.IB_ETH_TOO : HtrDefinitions.IB_RTH_ONLY);
        if (HtrEnums.BarType.MIN_5.equals(dataSeries.getBarType())) {
            ibController.reqHistoricalData(
                    dataSeries.getId() * HtrDefinitions.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    HtrDefinitions.IB_DURATION_10_DAY,
                    HtrDefinitions.IB_BAR_5_MIN,
                    dataSeries.getInstrument().getSecType().getIbWhatToShow(),
                    isUseRTH,
                    HtrDefinitions.IB_FORMAT_DATE_MILLIS);
        } else if (HtrEnums.BarType.MIN_60.equals(dataSeries.getBarType())) {
            int reqId = (dataSeries.getId() * HtrDefinitions.IB_REQUEST_MULT) + 4;
            Calendar his = HtrUtil.getCalendar();
            his.add(Calendar.MONTH, -3);
            for (int i = 0; i < 4; i++) {
                ibController.reqHistoricalData(
                        reqId--,
                        contract,
                        df.format(his.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                        HtrDefinitions.IB_DURATION_1_MONTH,
                        HtrDefinitions.IB_BAR_1_HOUR,
                        dataSeries.getInstrument().getSecType().getIbWhatToShow(),
                        isUseRTH,
                        HtrDefinitions.IB_FORMAT_DATE_MILLIS);
                his.add(Calendar.MONTH, 1);
                his.add(Calendar.DAY_OF_MONTH, -3); // some overlap
            }
            ibController.reqHistoricalData(
                    reqId,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    HtrDefinitions.IB_DURATION_1_MONTH,
                    HtrDefinitions.IB_BAR_1_HOUR,
                    dataSeries.getInstrument().getSecType().getIbWhatToShow(),
                    isUseRTH,
                    HtrDefinitions.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END backfillManual, series=" + dataSeries.getId() + ", symbol=" + dataSeries.getInstrument().getSymbol());
    }
}
