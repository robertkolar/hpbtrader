package com.highpowerbear.hpbtrader.shared.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rkolar
 */
public class HtrEnums {
    public enum Action {
        BUY,
        SELL,
        SSHORT
    }

    public enum OrderStatus {
        PENDINGSUBMIT("PendingSubmit", DisplayColor.DODGER_BLUE_BG),
        PENDINGCANCEL("PendingCancel", DisplayColor.ORANGE_BG),
        PRESUBMITTED("PreSubmitted", DisplayColor.DODGER_BLUE_BG),
        SUBMITTED("Submitted", DisplayColor.BLUE_BG),
        CANCELLED("Cancelled", DisplayColor.RED_BG),
        FILLED("Filled", DisplayColor.GREEN_BG),
        INACTIVE("Inactive", DisplayColor.BROWN_BG);

        private String displayName;
        private DisplayColor displayColor;

        OrderStatus(String displayName, DisplayColor displayColor) {
            this.displayName = displayName;
            this.displayColor = displayColor;
        }

        public String getDisplayName() {
            return displayName;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }

    public enum OrderType {
        MKT,
        MKTCLS,
        LMT,
        LMTCLS,
        PEGMKT,
        SCALE,
        STP,
        STPLMT,
        TRAIL,
        REL,
        VWAP,
        TRAILLIMIT
    }

    public enum Tif {
        DAY,
        GTC,
        IOC,
        GTD
    }

    public enum OptionType {
        CALL("C"),
        PUT("P");

        private String code;

        OptionType(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    public enum Multiplier {
        M_100("100");

        String value;

        Multiplier(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Interval {
        MIN5(300000),
        MIN60(3600000);
        
        private long millis;
        Interval(long millis) {
            this.millis = millis;
        }
        public long getMillis() {
            return millis;
        }
    }
    
    public enum SecType {
        STK("STK", 100, HtrDefinitions.IB_BAR_TYPE_TRADES),
        OPT("OPT", 1, HtrDefinitions.IB_BAR_TYPE_TRADES),
        FUT("FUT", 1, HtrDefinitions.IB_BAR_TYPE_TRADES),
        FOP("FOP", 1, HtrDefinitions.IB_BAR_TYPE_TRADES),
        CASH("CSH", 100000, HtrDefinitions.IB_BAR_TYPE_ASK);

        private String displayName;
        private int defaultTradingQuantity;
        private String ibBarType;

        SecType(String displayName, int defaultTradingQuantity, String ibBarType) {
            this.displayName = displayName;
            this.defaultTradingQuantity = defaultTradingQuantity;
            this.ibBarType = ibBarType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDefaultTradingQuantity() {
            return defaultTradingQuantity;
        }

        public String getIbBarType() {
            return ibBarType;
        }
    }
    
    public enum Currency {
        USD,
        EUR,
        AUD,
        GBP,
        CHF,
        CAD,
        JPY
    }
    
    public enum OrderAction {
        BTO,
        BTC,
        STO,
        STC,
        BREV,
        SREV
    }
    
    public enum IbOrderStatus {
        NEW("new", DisplayColor.MAGENTA_BG),
        NEW_RETRY("newRetry", DisplayColor.MAGENTA_BG),
        SUBMIT_REQ("submitReq", DisplayColor.DODGER_BLUE_BG),
        SUBMITTED("submitted", DisplayColor.BLUE_BG),
        FILLED("filled", DisplayColor.GREEN_BG),
        CANCEL_REQ("cancelReq", DisplayColor.MAGENTA_BG),
        CANCELED("canceled", DisplayColor.RED_BG),
        UNKNOWN("unknown", DisplayColor.BROWN_BG);
        
        private String displayName;
        private DisplayColor displayColor;
        
        IbOrderStatus(String displayName, DisplayColor displayColor) {
            this.displayName = displayName;
            this.displayColor = displayColor;
        }
        public String getDisplayName() {
            return displayName;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }
    
    public enum SubmitType {
        AUTO("auto"),
        MANUAL("manual");
        
        private String displayName;
        
        SubmitType(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum StrategyType {
        LUXOR("luxor", "20, 50, 1.3d, 2.2d, 4, 6"), // emaShort, emaLong, stopPct, targetPct, startHourEst, durationHours
        MACD_CROSS("macdCross", "50, 50"), // stochOversold, stochOverbougth
        TEST("test", "1.3d, 2.2d"); // stopPct, targetPct
        
        private String displayName;
        private String defaultParams;

        StrategyType(String displayName, String defaultParams) {
            this.displayName = displayName;
            this.defaultParams = defaultParams;
        }
        public String getDisplayName() {
            return displayName;
        }

        public String getDefaultParams() {
            return defaultParams;
        }
    }
    
    public enum StrategyMode {
        IB(DisplayColor.ORANGE_BG),
        SIM(null),
        BTEST(null);

        private DisplayColor displayColor;

        StrategyMode(DisplayColor displayColor) {
            this.displayColor = displayColor;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }
    
    public static final List<StrategyMode> selectableStrategyModes = new ArrayList<>();
    static {
        selectableStrategyModes.add(StrategyMode.IB);
        selectableStrategyModes.add(StrategyMode.SIM);
    }
    
    public enum TradeType {
        LONG("L", DisplayColor.BLUE_BG),
        SHORT("S", DisplayColor.BROWN_BG);

        private String displayName;
        private DisplayColor displayColor;

        TradeType(String displayName, DisplayColor displayColor) {
            this.displayName = displayName;
            this.displayColor = displayColor;
        }
        public String getDisplayName() {
            return displayName;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }
    
    public enum TradeStatus {
        INIT_OPEN("initOpen", DisplayColor.BLUE_BG),
        OPEN("open", DisplayColor.GREEN_BG),
        INIT_CLOSE("initClose", DisplayColor.ORANGE_BG),
        CLOSED("closed", DisplayColor.BROWN_BG),
        CNC_CLOSED("cncClosed", DisplayColor.RED_BG),
        ERR_CLOSED("errClosed", DisplayColor.RED_BG);
        
        private String displayName;
        private DisplayColor displayColor;
        
        TradeStatus(String displayName, DisplayColor displayColor) {
            this.displayName = displayName;
            this.displayColor = displayColor;
        }
        public String getDisplayName() {
            return displayName;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }
    
    public static final List<Integer> tradingQuantities = new ArrayList<>();
    static {
        tradingQuantities.add(1);
        tradingQuantities.add(10);
        tradingQuantities.add(100);
        tradingQuantities.add(1000);
        tradingQuantities.add(10000);
        tradingQuantities.add(100000);
    }
    
    public enum FutureMultiplier {
        ES(50),
        NQ(20),
        YM(5),
        ZB(1000),
        GC(100),
        CL(1000);

        private Integer multiplier;
        
        FutureMultiplier(Integer multiplier) {
            this.multiplier = multiplier;
        }
        public static Integer getMultiplierBySymbol(String futSymbol) {
            return (futSymbol != null ? Arrays.asList(FutureMultiplier.values()).stream().filter(fm -> futSymbol.startsWith(fm.toString())).findAny().get().multiplier : 1);
        }
    }
    
    public enum MiniOption {
        AMZN7, AAPL7, GOOG7,  GLD7, SPY7;
        public static boolean isMiniOption(String optionSymbol) {
            return optionSymbol != null && Arrays.asList(MiniOption.values()).stream().filter(mo -> optionSymbol.startsWith(mo.name())).findAny().isPresent();
        }
    }

    public enum DataChangeEvent {
        BAR_UPDATE,
        STRATEGY_UPDATE
    }

    public enum RealtimeFieldName {
        BID,
        ASK,
        LAST,
        CLOSE,
        BID_SIZE,
        ASK_SIZE,
        LAST_SIZE,
        VOLUME,
        CHANGE_PCT
    }

    public enum RealtimeStatus {
        UPTICK(DisplayColor.LIME),
        DOWNTICK(DisplayColor.ORANGE),
        UNCHANGED(DisplayColor.YELLOW),
        POSITIVE(DisplayColor.BLUE),
        NEGATIVE(DisplayColor.RED);

        private DisplayColor displayColor;

        RealtimeStatus(DisplayColor displayColor) {
            this.displayColor = displayColor;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }

    public enum Exchange {
        SMART,
        IDEALPRO,
        GLOBEX,
        ECBOT,
        NYMEX
    }

    public enum ValueStatus {
        UPTICK,
        DOWNTICK,
        UNCHANGED
    }

    public enum DisplayColor {
        MAGENTA,
        MAGENTA_BG,
        BLUE,
        BLUE_BG,
        DARK_BLUE,
        DARK_BLUE_BG,
        GREEN,
        GREEN_BG,
        DARK_GREEN,
        DARK_GREEN_BG,
        RED,
        RED_BG,
        DARK_RED,
        DARK_RED_BG,
        ORANGE,
        ORANGE_BG,
        DARK_ORANGE,
        DARK_ORANGE_BG,
        DARK_CYAN,
        DARK_CYAN_BG,
        LIME,
        LIME_BG,
        YELLOW,
        YELLOW_BG,
        BROWN,
        BROWN_BG,
        DODGER_BLUE,
        DODGER_BLUE_BG;
    }

    public enum OptRequestIdOffset {
        CHAIN_CALL_THIS_MONTH(1),
        CHAIN_CALL_NEXT_MONTH(2),
        CHAIN_PUT_THIS_MONTH(11),
        CHAIN_PUT_NEXT_MONTH(12),
        MKTDATA_UNDERLYING(10),
        MKTDATA_CALL_ACTIVE(20),
        MKTDATA_PUT_ACTIVE(30);

        private Integer value;

        OptRequestIdOffset(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public enum OptExpiryDistance {
        FRONT_WEEK(0),
        ONE_WEEK_OUT(1),
        TWO_WEEK_OUT(2),
        THREE_WEEK_OUT(3);

        private Integer week;

        OptExpiryDistance(Integer number) {
            this.week = number;
        }

        public Integer getWeek() {
            return week;
        }
    }
}
