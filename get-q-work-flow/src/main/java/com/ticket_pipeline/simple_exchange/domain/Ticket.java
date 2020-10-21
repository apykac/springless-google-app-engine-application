package com.ticket_pipeline.simple_exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ticket_pipeline.simple_utils.clean.Cleanable;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket implements Cleanable {
    private Long id;
    private List<String> tags;
    private Long groupId;
    private Long requesterId;
    private Long assigneeId;
    private Status status;
    private Type type;

    public Long getId() {
        return id;
    }

    public List<String> getTags() {
        return tags;
    }

    public Status getStatus() {
        return status;
    }

    @JsonProperty("requester_id")
    public Long getRequesterId() {
        return requesterId;
    }

    @JsonProperty("assignee_id")
    public Long getAssigneeId() {
        return this.assigneeId;
    }

    @JsonProperty("group_id")
    public Long getGroupId() {
        return this.groupId;
    }

    public Type getType() {
        return type;
    }

    public Ticket setId(Long id) {
        this.id = id;
        return this;
    }

    public Ticket setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public Ticket setGroupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public Ticket setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
        return this;
    }

    public Ticket setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public Ticket setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Ticket setType(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) &&
                Objects.equals(tags, ticket.tags) &&
                Objects.equals(groupId, ticket.groupId) &&
                Objects.equals(requesterId, ticket.requesterId) &&
                Objects.equals(assigneeId, ticket.assigneeId) &&
                status == ticket.status &&
                type == ticket.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tags, groupId, requesterId, assigneeId, status, type);
    }

    @Override
    public void clean() {
        id = null;
        if (tags != null) {
            tags.clear();
            tags = null;
        }
        groupId = null;
        requesterId = null;
        assigneeId = null;
        status = null;
        type = null;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", tags=" + tags +
                ", groupId=" + groupId +
                ", requesterId=" + requesterId +
                ", assigneeId=" + assigneeId +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
