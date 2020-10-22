package com.ticket_pipeline.simple_exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ticket_pipeline.simple_utils.clean.Cleanable;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketList implements Cleanable {
    private List<Ticket> tickets;
    private String nextPage; //varchar(255)
    private String error;

    @JsonProperty("tickets")
    public List<Ticket> getTickets() {
        return tickets;
    }

    @JsonProperty("next_page")
    public String getNextPage() {
        return nextPage;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    public TicketList setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        return this;
    }

    public TicketList setNextPage(String nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    public TicketList setError(String error) {
        this.error = error;
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
