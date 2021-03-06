/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.api.query;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.kernel.impl.query.clientconnection.ClientConnectionInfo;

public class QuerySnapshot
{
    private final ExecutingQuery query;
    private final PlannerInfo plannerInfo;
    private final long planningTimeMillis;
    private final long elapsedTimeMillis;
    private final long cpuTimeMillis;
    private final long waitTimeMillis;
    private final String status;
    private final Map<String,Object> resourceInfo;
    private final long activeLockCount;

    QuerySnapshot(
            ExecutingQuery query,
            PlannerInfo plannerInfo,
            long planningTimeMillis,
            long elapsedTimeMillis,
            long cpuTimeMillis,
            long waitTimeMillis,
            String status,
            Map<String,Object> resourceInfo,
            long activeLockCount )
    {
        this.query = query;
        this.plannerInfo = plannerInfo;
        this.planningTimeMillis = planningTimeMillis;
        this.elapsedTimeMillis = elapsedTimeMillis;
        this.cpuTimeMillis = cpuTimeMillis;
        this.waitTimeMillis = waitTimeMillis;
        this.status = status;
        this.resourceInfo = resourceInfo;
        this.activeLockCount = activeLockCount;
    }

    public long internalQueryId()
    {
        return query.internalQueryId();
    }

    public String queryText()
    {
        return query.queryText();
    }

    public Map<String,Object> queryParameters()
    {
        return query.queryParameters();
    }

    public String username()
    {
        return query.username();
    }

    public ClientConnectionInfo clientConnection()
    {
        return query.clientConnection();
    }

    public Map<String,Object> transactionAnnotationData()
    {
        return query.transactionAnnotationData();
    }

    public long activeLockCount()
    {
        return activeLockCount;
    }

    public String planner()
    {
        return plannerInfo == null ? null : plannerInfo.planner();
    }

    public String runtime()
    {
        return plannerInfo == null ? null : plannerInfo.runtime();
    }

    public List<Map<String,String>> indexes()
    {
        if ( plannerInfo == null )
        {
            return Collections.emptyList();
        }
        return plannerInfo.indexes().stream()
                .map( IndexUsage::asMap )
                .collect( Collectors.toList() );
    }

    public String status()
    {
        return status;
    }

    public Map<String,Object> resourceInformation()
    {
        return resourceInfo;
    }

    public long startTimestampMillis()
    {
        return query.startTimestampMillis();
    }

    /**
     * The time spent planning the query, before the query actually starts executing.
     *
     * @return the time in milliseconds spent planning the query.
     */
    public long planningTimeMillis()
    {
        return planningTimeMillis;
    }

    /**
     * The time that has been spent waiting on locks or other queries, as opposed to actively executing this query.
     *
     * @return the time in milliseconds spent waiting on locks.
     */
    public long waitTimeMillis()
    {
        return waitTimeMillis;
    }

    /**
     * The time (wall time) that has elapsed since the execution of this query started.
     *
     * @return the time in milliseconds since execution of this query started.
     */
    public long elapsedTimeMillis()
    {
        return elapsedTimeMillis;
    }

    /**
     * Time that the CPU has actively spent working on things related to this query.
     *
     * @return the time in milliseconds that the CPU has spent on this query.
     */
    public long cpuTimeMillis()
    {
        return cpuTimeMillis;
    }

    /**
     * Time from the start of this query that the computer spent doing other things than working on this query, even
     * though the query was runnable.
     * <p>
     * In rare cases the idle time can be negative. This is due to the fact that the Thread does not go to sleep
     * immediately after we start measuring the wait-time, there is still some "lock bookkeeping time" that counts as
     * both cpu time (because the CPU is actually actively working on this thread) and wait time (because the query is
     * actually waiting on the lock rather than doing active work). In most cases such "lock bookkeeping time" is going
     * to be dwarfed by the idle time.
     *
     * @return the time in milliseconds that this query was de-scheduled.
     */
    public long idleTimeMillis()
    {
        return elapsedTimeMillis - cpuTimeMillis - waitTimeMillis;
    }
}
