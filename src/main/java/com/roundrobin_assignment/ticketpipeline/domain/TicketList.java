package com.roundrobin_assignment.ticketpipeline.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.roundrobin_assignment.ticketpipeline.clean.Cleanable;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketList implements Cleanable {
    private List<Ticket> tickets;
    private String nextPage; //varchar(255)

    @JsonProperty("tickets")
    public List<Ticket> getTickets() {
        return tickets;
    }

    @JsonProperty("next_page")
    public String getNextPage() {
        return nextPage;
    }

    public TicketList setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        return this;
    }

    public TicketList setNextPage(String nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketList that = (TicketList) o;
        return Objects.equals(tickets, that.tickets) &&
                Objects.equals(nextPage, that.nextPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickets, nextPage);
    }

    @Override
    public void clean() {
        if (tickets != null) {
            tickets.forEach(Ticket::clean);
            tickets.clear();
            tickets = null;
        }
        nextPage = null;
    }

    @Override
    public String toString() {
        return "TicketList{" +
                "tickets=" + tickets +
                ", nextPage='" + nextPage + '\'' +
                '}';
    }
}
