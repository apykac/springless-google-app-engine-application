package com.roundrobin_assignment.ticketpipeline.server.controller;

import com.roundrobin_assignment.ticketpipeline.server.EntryPoint;

import java.util.List;

public interface Controller {
    List<EntryPoint> entryPoints();
}
