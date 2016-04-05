package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.mktdata.process.RtDataController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by robertk on 2.12.2015.
 */
@ApplicationScoped
@Path("series")
public class DataSeriesService {

    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private HistDataController histDataController;
    @Inject private RtDataController rtDataController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestList<DataSeries> getSeriesList(@QueryParam("inactiveToo") boolean inactiveToo) {
        List<DataSeries> dataSeriesList = dataSeriesDao.getAllSeries(inactiveToo);
        return new RestList<>(dataSeriesList, (long) dataSeriesList.size());
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{seriesId}/backfill")
    public Response backfillBars(@PathParam("seriesId") Integer seriesId) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        histDataController.backfill(dataSeries);
        return Response.ok().build();
    }

    @GET
    @Path("{seriesId}/bars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBars(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, numBars);
        return Response.ok(new RestList<>(dataSeriesDao.getBars(dataSeries, numBars), (long) dataBars.size())).build();
    }

    @GET
    @Path("{seriesId}/pagedbars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedBars(@PathParam("seriesId") Integer seriesId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = (start == null ? 0 : start);
        limit = (limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit);
        return Response.ok(new RestList<>(dataSeriesDao.getPagedBars(dataSeries, start, limit), dataSeriesDao.getNumBars(dataSeries))).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{seriesId}/rtdata/toggle")
    public Response toggleRtData(@PathParam("seriesId") Integer seriesId) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        rtDataController.toggleRealtimeData(dataSeries);
        return Response.ok().build();
    }

    @GET
    @Path("{seriesId}/rtdata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRtData(@PathParam("seriesId") Integer seriesId) {
        List<RealtimeData> rtDataList = rtDataController.getRealtimeDataList();
        return Response.ok(new RestList<>(rtDataList, (long) rtDataList.size())).build();
    }
}