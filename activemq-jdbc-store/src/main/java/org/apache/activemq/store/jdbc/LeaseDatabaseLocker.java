begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IOExceptionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ServiceStopper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Represents an exclusive lease on a database to avoid multiple brokers running  * against the same logical database.  *   * @org.apache.xbean.XBean element="lease-database-locker"  *   */
end_comment

begin_class
specifier|public
class|class
name|LeaseDatabaseLocker
extends|extends
name|AbstractJDBCLocker
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LeaseDatabaseLocker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|maxAllowableDiffFromDBTime
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|diffFromCurrentTime
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|protected
name|String
name|leaseHolderId
decl_stmt|;
specifier|protected
name|boolean
name|handleStartException
decl_stmt|;
specifier|public
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|lockAcquireSleepInterval
operator|<
name|lockable
operator|.
name|getLockKeepAlivePeriod
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"LockableService keep alive period: "
operator|+
name|lockable
operator|.
name|getLockKeepAlivePeriod
argument_list|()
operator|+
literal|", which renews the lease, is greater than lockAcquireSleepInterval: "
operator|+
name|lockAcquireSleepInterval
operator|+
literal|", the lease duration. These values will allow the lease to expire."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" attempting to acquire exclusive lease to become the master"
argument_list|)
expr_stmt|;
name|String
name|sql
init|=
name|getStatements
argument_list|()
operator|.
name|getLeaseObtainStatement
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" locking Query is "
operator|+
name|sql
argument_list|)
expr_stmt|;
name|long
name|now
init|=
literal|0l
decl_stmt|;
while|while
condition|(
operator|!
name|isStopping
argument_list|()
condition|)
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|getConnection
argument_list|()
expr_stmt|;
name|initTimeDiff
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|statement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|setQueryTimeout
argument_list|(
name|statement
argument_list|)
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|diffFromCurrentTime
expr_stmt|;
name|statement
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|getLeaseHolderId
argument_list|()
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|now
operator|+
name|lockAcquireSleepInterval
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setLong
argument_list|(
literal|3
argument_list|,
name|now
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|statement
operator|.
name|executeUpdate
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|1
condition|)
block|{
comment|// we got the lease, verify we still have it
if|if
condition|(
name|keepAlive
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
name|reportLeasOwnerShipAndDuration
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" lease acquire failure: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|isStopping
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Cannot start broker as being asked to shut down. "
operator|+
literal|"Interrupted attempt to acquire lock: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|handleStartException
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|close
argument_list|(
name|statement
argument_list|)
expr_stmt|;
name|close
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" failed to acquire lease.  Sleeping for "
operator|+
name|lockAcquireSleepInterval
operator|+
literal|" milli(s) before trying again..."
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lockAcquireSleepInterval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isStopping
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" failing lease acquire due to stop"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|", becoming master with lease expiry "
operator|+
operator|new
name|Date
argument_list|(
name|now
operator|+
name|lockAcquireSleepInterval
argument_list|)
operator|+
literal|" on dataSource: "
operator|+
name|dataSource
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reportLeasOwnerShipAndDuration
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|SQLException
block|{
name|PreparedStatement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|statement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|getStatements
argument_list|()
operator|.
name|getLeaseOwnerStatement
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" Lease held by "
operator|+
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
operator|+
literal|" till "
operator|+
operator|new
name|Date
argument_list|(
name|resultSet
operator|.
name|getLong
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|close
argument_list|(
name|statement
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|long
name|initTimeDiff
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|Long
operator|.
name|MAX_VALUE
operator|==
name|diffFromCurrentTime
condition|)
block|{
if|if
condition|(
name|maxAllowableDiffFromDBTime
operator|>
literal|0
condition|)
block|{
name|diffFromCurrentTime
operator|=
name|determineTimeDifference
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diffFromCurrentTime
operator|=
literal|0l
expr_stmt|;
block|}
block|}
return|return
name|diffFromCurrentTime
return|;
block|}
specifier|protected
name|long
name|determineTimeDifference
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
init|(
name|PreparedStatement
name|statement
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|getStatements
argument_list|()
operator|.
name|getCurrentDateTime
argument_list|()
argument_list|)
init|;
name|ResultSet
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|()
init|)
block|{
name|long
name|result
init|=
literal|0l
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|Timestamp
name|timestamp
init|=
name|resultSet
operator|.
name|getTimestamp
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|diff
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|timestamp
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|diff
argument_list|)
operator|>
name|maxAllowableDiffFromDBTime
condition|)
block|{
comment|// off by more than maxAllowableDiffFromDBTime so lets adjust
name|result
operator|=
operator|(
operator|-
name|diff
operator|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" diff adjust from db: "
operator|+
name|result
operator|+
literal|", db time: "
operator|+
name|timestamp
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
specifier|public
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|lockable
operator|.
name|getBrokerService
argument_list|()
operator|!=
literal|null
operator|&&
name|lockable
operator|.
name|getBrokerService
argument_list|()
operator|.
name|isRestartRequested
argument_list|()
condition|)
block|{
comment|// keep our lease for restart
return|return;
block|}
name|releaseLease
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|releaseLease
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|getConnection
argument_list|()
expr_stmt|;
name|statement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|getStatements
argument_list|()
operator|.
name|getLeaseUpdateStatement
argument_list|()
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setString
argument_list|(
literal|3
argument_list|,
name|getLeaseHolderId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|statement
operator|.
name|executeUpdate
argument_list|()
operator|==
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|", released lease"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|" failed to release lease: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|statement
argument_list|)
expr_stmt|;
name|close
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|keepAlive
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|getStatements
argument_list|()
operator|.
name|getLeaseUpdateStatement
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|", lease keepAlive Query is "
operator|+
name|sql
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|getConnection
argument_list|()
expr_stmt|;
name|initTimeDiff
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|statement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|setQueryTimeout
argument_list|(
name|statement
argument_list|)
expr_stmt|;
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|diffFromCurrentTime
decl_stmt|;
name|statement
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|getLeaseHolderId
argument_list|()
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|now
operator|+
name|lockAcquireSleepInterval
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setString
argument_list|(
literal|3
argument_list|,
name|getLeaseHolderId
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|statement
operator|.
name|executeUpdate
argument_list|()
operator|==
literal|1
operator|)
expr_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
name|reportLeasOwnerShipAndDuration
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|getLeaseHolderId
argument_list|()
operator|+
literal|", failed to update lease: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|IOException
name|ioe
init|=
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|lockable
operator|.
name|getBrokerService
argument_list|()
operator|.
name|handleIOException
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|statement
argument_list|)
expr_stmt|;
name|close
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|String
name|getLeaseHolderId
parameter_list|()
block|{
if|if
condition|(
name|leaseHolderId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|lockable
operator|.
name|getBrokerService
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|leaseHolderId
operator|=
name|lockable
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getBrokerName
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|leaseHolderId
return|;
block|}
specifier|public
name|void
name|setLeaseHolderId
parameter_list|(
name|String
name|leaseHolderId
parameter_list|)
block|{
name|this
operator|.
name|leaseHolderId
operator|=
name|leaseHolderId
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxAllowableDiffFromDBTime
parameter_list|()
block|{
return|return
name|maxAllowableDiffFromDBTime
return|;
block|}
specifier|public
name|void
name|setMaxAllowableDiffFromDBTime
parameter_list|(
name|int
name|maxAllowableDiffFromDBTime
parameter_list|)
block|{
name|this
operator|.
name|maxAllowableDiffFromDBTime
operator|=
name|maxAllowableDiffFromDBTime
expr_stmt|;
block|}
specifier|public
name|boolean
name|isHandleStartException
parameter_list|()
block|{
return|return
name|handleStartException
return|;
block|}
specifier|public
name|void
name|setHandleStartException
parameter_list|(
name|boolean
name|handleStartException
parameter_list|)
block|{
name|this
operator|.
name|handleStartException
operator|=
name|handleStartException
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LeaseDatabaseLocker owner:"
operator|+
name|leaseHolderId
operator|+
literal|",duration:"
operator|+
name|lockAcquireSleepInterval
operator|+
literal|",renew:"
operator|+
name|lockAcquireSleepInterval
return|;
block|}
block|}
end_class

end_unit

