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
name|kahadb
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicInteger
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
name|Service
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|TransportAcceptListener
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
name|transport
operator|.
name|TransportFactory
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
name|transport
operator|.
name|TransportListener
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
name|transport
operator|.
name|TransportServer
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
name|Callback
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|DataFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|Location
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|ReplicationTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBFileInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBHeader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBJournalLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBJournalUpdate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBSlaveInit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBSlaveInitResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|store
operator|.
name|KahaDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
specifier|public
class|class
name|ReplicationMaster
implements|implements
name|Service
implements|,
name|ClusterListener
implements|,
name|ReplicationTarget
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ReplicationService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReplicationService
name|replicationService
decl_stmt|;
specifier|private
name|Object
name|serverMutex
init|=
operator|new
name|Object
argument_list|()
block|{}
decl_stmt|;
specifier|private
name|TransportServer
name|server
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
name|sessions
init|=
operator|new
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|nextSnapshotId
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Location
argument_list|,
name|CountDownLatch
argument_list|>
name|requestMap
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Location
argument_list|,
name|CountDownLatch
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ReplicationMaster
parameter_list|(
name|ReplicationService
name|replicationService
parameter_list|)
block|{
name|this
operator|.
name|replicationService
operator|=
name|replicationService
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|serverMutex
init|)
block|{
name|server
operator|=
name|TransportFactory
operator|.
name|bind
argument_list|(
operator|new
name|URI
argument_list|(
name|replicationService
operator|.
name|getUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|TransportAcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|serverMutex
init|)
block|{
name|ReplicationSession
name|session
init|=
operator|new
name|ReplicationSession
argument_list|(
name|transport
argument_list|)
decl_stmt|;
name|session
operator|.
name|start
argument_list|()
expr_stmt|;
name|addSession
argument_list|(
name|session
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
name|info
argument_list|(
literal|"Could not accept replication connection from slave at "
operator|+
name|transport
operator|.
name|getRemoteAddress
argument_list|()
operator|+
literal|", due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not accept replication connection: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|replicationService
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|setReplicationTarget
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isStarted
parameter_list|()
block|{
synchronized|synchronized
init|(
name|serverMutex
init|)
block|{
return|return
name|server
operator|!=
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|replicationService
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|setReplicationTarget
argument_list|(
literal|null
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|serverMutex
init|)
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
name|sessionsSnapshot
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|sessions
init|)
block|{
name|sessionsSnapshot
operator|=
name|this
operator|.
name|sessions
expr_stmt|;
block|}
for|for
control|(
name|ReplicationSession
name|session
range|:
name|sessionsSnapshot
control|)
block|{
name|session
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|addSession
parameter_list|(
name|ReplicationSession
name|session
parameter_list|)
block|{
synchronized|synchronized
init|(
name|sessions
init|)
block|{
name|sessions
operator|=
operator|new
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
argument_list|(
name|sessions
argument_list|)
expr_stmt|;
name|sessions
operator|.
name|add
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|removeSession
parameter_list|(
name|ReplicationSession
name|session
parameter_list|)
block|{
synchronized|synchronized
init|(
name|sessions
init|)
block|{
name|sessions
operator|=
operator|new
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
argument_list|(
name|sessions
argument_list|)
expr_stmt|;
name|sessions
operator|.
name|remove
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onClusterChange
parameter_list|(
name|ClusterState
name|config
parameter_list|)
block|{
comment|// For now, we don't really care about changes in the slave config..
block|}
comment|/** 	 * This is called by the Journal so that we can replicate the update to the  	 * slaves. 	 */
specifier|public
name|void
name|replicate
parameter_list|(
name|Location
name|location
parameter_list|,
name|ByteSequence
name|sequence
parameter_list|,
name|boolean
name|sync
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
name|sessionsSnapshot
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|sessions
init|)
block|{
comment|// Hurrah for copy on write..
name|sessionsSnapshot
operator|=
name|this
operator|.
name|sessions
expr_stmt|;
block|}
comment|// We may be configured to always do async replication..
if|if
condition|(
name|replicationService
operator|.
name|isAsyncReplication
argument_list|()
condition|)
block|{
name|sync
operator|=
literal|false
expr_stmt|;
block|}
name|CountDownLatch
name|latch
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|requestMap
init|)
block|{
name|requestMap
operator|.
name|put
argument_list|(
name|location
argument_list|,
name|latch
argument_list|)
expr_stmt|;
block|}
block|}
name|ReplicationFrame
name|frame
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ReplicationSession
name|session
range|:
name|sessionsSnapshot
control|)
block|{
if|if
condition|(
name|session
operator|.
name|subscribedToJournalUpdates
operator|.
name|get
argument_list|()
condition|)
block|{
comment|// Lazy create the frame since we may have not avilable sessions to send to.
if|if
condition|(
name|frame
operator|==
literal|null
condition|)
block|{
name|frame
operator|=
operator|new
name|ReplicationFrame
argument_list|()
expr_stmt|;
name|frame
operator|.
name|setHeader
argument_list|(
operator|new
name|PBHeader
argument_list|()
operator|.
name|setType
argument_list|(
name|PBType
operator|.
name|JOURNAL_UPDATE
argument_list|)
argument_list|)
expr_stmt|;
name|PBJournalUpdate
name|payload
init|=
operator|new
name|PBJournalUpdate
argument_list|()
decl_stmt|;
name|payload
operator|.
name|setLocation
argument_list|(
name|ReplicationSupport
operator|.
name|convert
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|.
name|setData
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|protobuf
operator|.
name|Buffer
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|.
name|setSendAck
argument_list|(
name|sync
argument_list|)
expr_stmt|;
name|frame
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
comment|// TODO: use async send threads so that the frames can be pushed out in parallel.
try|try
block|{
name|session
operator|.
name|setLastUpdateLocation
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|session
operator|.
name|transport
operator|.
name|oneway
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|session
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|sync
condition|)
block|{
try|try
block|{
name|int
name|timeout
init|=
literal|500
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|latch
operator|.
name|await
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
synchronized|synchronized
init|(
name|requestMap
init|)
block|{
name|requestMap
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
operator|!
name|isStarted
argument_list|()
condition|)
block|{
return|return;
block|}
name|counter
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|counter
operator|%
literal|10
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"KahaDB is waiting for slave to come online. "
operator|+
operator|(
name|timeout
operator|*
name|counter
operator|/
literal|1000.f
operator|)
operator|+
literal|" seconds have elapsed."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|private
name|void
name|ackAllFromTo
parameter_list|(
name|Location
name|lastAck
parameter_list|,
name|Location
name|newAck
parameter_list|)
block|{
if|if
condition|(
name|replicationService
operator|.
name|isAsyncReplication
argument_list|()
condition|)
block|{
return|return;
block|}
name|ArrayList
argument_list|<
name|Entry
argument_list|<
name|Location
argument_list|,
name|CountDownLatch
argument_list|>
argument_list|>
name|entries
decl_stmt|;
synchronized|synchronized
init|(
name|requestMap
init|)
block|{
name|entries
operator|=
operator|new
name|ArrayList
argument_list|<
name|Entry
argument_list|<
name|Location
argument_list|,
name|CountDownLatch
argument_list|>
argument_list|>
argument_list|(
name|requestMap
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|inRange
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Location
argument_list|,
name|CountDownLatch
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|Location
name|l
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|inRange
condition|)
block|{
if|if
condition|(
name|lastAck
operator|==
literal|null
operator|||
name|lastAck
operator|.
name|compareTo
argument_list|(
name|l
argument_list|)
operator|<
literal|0
condition|)
block|{
name|inRange
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|inRange
condition|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|newAck
operator|!=
literal|null
operator|&&
name|l
operator|.
name|compareTo
argument_list|(
name|newAck
argument_list|)
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
class|class
name|ReplicationSession
implements|implements
name|Service
implements|,
name|TransportListener
block|{
specifier|private
specifier|final
name|Transport
name|transport
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|subscribedToJournalUpdates
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|stopped
decl_stmt|;
specifier|private
name|File
name|snapshotFile
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|journalReplicatedFiles
decl_stmt|;
specifier|private
name|Location
name|lastAckLocation
decl_stmt|;
specifier|private
name|Location
name|lastUpdateLocation
decl_stmt|;
specifier|private
name|boolean
name|online
decl_stmt|;
specifier|public
name|ReplicationSession
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|setLastUpdateLocation
parameter_list|(
name|Location
name|lastUpdateLocation
parameter_list|)
block|{
name|this
operator|.
name|lastUpdateLocation
operator|=
name|lastUpdateLocation
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|transport
operator|.
name|setTransportListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|stopped
condition|)
block|{
name|stopped
operator|=
literal|true
expr_stmt|;
name|deleteReplicationData
argument_list|()
expr_stmt|;
name|transport
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|synchronized
specifier|private
name|void
name|onJournalUpdateAck
parameter_list|(
name|ReplicationFrame
name|frame
parameter_list|,
name|PBJournalLocation
name|location
parameter_list|)
block|{
name|Location
name|ack
init|=
name|ReplicationSupport
operator|.
name|convert
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|online
condition|)
block|{
name|ackAllFromTo
argument_list|(
name|lastAckLocation
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
name|lastAckLocation
operator|=
name|ack
expr_stmt|;
block|}
specifier|synchronized
specifier|private
name|void
name|onSlaveOnline
parameter_list|(
name|ReplicationFrame
name|frame
parameter_list|)
block|{
name|deleteReplicationData
argument_list|()
expr_stmt|;
name|online
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|lastAckLocation
operator|!=
literal|null
condition|)
block|{
name|ackAllFromTo
argument_list|(
literal|null
argument_list|,
name|lastAckLocation
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
try|try
block|{
name|ReplicationFrame
name|frame
init|=
operator|(
name|ReplicationFrame
operator|)
name|command
decl_stmt|;
switch|switch
condition|(
name|frame
operator|.
name|getHeader
argument_list|()
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|SLAVE_INIT
case|:
name|onSlaveInit
argument_list|(
name|frame
argument_list|,
operator|(
name|PBSlaveInit
operator|)
name|frame
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|SLAVE_ONLINE
case|:
name|onSlaveOnline
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|FILE_TRANSFER
case|:
name|onFileTransfer
argument_list|(
name|frame
argument_list|,
operator|(
name|PBFileInfo
operator|)
name|frame
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|JOURNAL_UPDATE_ACK
case|:
name|onJournalUpdateAck
argument_list|(
name|frame
argument_list|,
operator|(
name|PBJournalLocation
operator|)
name|frame
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
break|break;
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
literal|"Slave request failed: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|failed
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|failed
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|failed
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{ 			}
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{ 		}
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{ 		}
specifier|private
name|void
name|deleteReplicationData
parameter_list|()
block|{
if|if
condition|(
name|snapshotFile
operator|!=
literal|null
condition|)
block|{
name|snapshotFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|snapshotFile
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|journalReplicatedFiles
operator|!=
literal|null
condition|)
block|{
name|journalReplicatedFiles
operator|=
literal|null
expr_stmt|;
name|updateJournalReplicatedFiles
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|onSlaveInit
parameter_list|(
name|ReplicationFrame
name|frame
parameter_list|,
name|PBSlaveInit
name|slaveInit
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Start sending journal updates to the slave.
name|subscribedToJournalUpdates
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// We could look at the slave state sent in the slaveInit and decide
comment|// that a full sync is not needed..
comment|// but for now we will do a full sync every time.
name|ReplicationFrame
name|rc
init|=
operator|new
name|ReplicationFrame
argument_list|()
decl_stmt|;
specifier|final
name|PBSlaveInitResponse
name|rcPayload
init|=
operator|new
name|PBSlaveInitResponse
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setHeader
argument_list|(
operator|new
name|PBHeader
argument_list|()
operator|.
name|setType
argument_list|(
name|PBType
operator|.
name|SLAVE_INIT_RESPONSE
argument_list|)
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setPayload
argument_list|(
name|rcPayload
argument_list|)
expr_stmt|;
comment|// Setup a map of all the files that the slave has
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|PBFileInfo
argument_list|>
name|slaveFiles
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PBFileInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PBFileInfo
name|info
range|:
name|slaveInit
operator|.
name|getCurrentFilesList
argument_list|()
control|)
block|{
name|slaveFiles
operator|.
name|put
argument_list|(
name|info
operator|.
name|getName
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|final
name|KahaDBStore
name|store
init|=
name|replicationService
operator|.
name|getStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|checkpoint
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This call back is executed once the checkpoint is
comment|// completed and all data has been synced to disk,
comment|// but while a lock is still held on the store so
comment|// that no updates are done while we are in this
comment|// method.
name|KahaDBStore
name|store
init|=
name|replicationService
operator|.
name|getStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastAckLocation
operator|==
literal|null
condition|)
block|{
name|lastAckLocation
operator|=
name|store
operator|.
name|getLastUpdatePosition
argument_list|()
expr_stmt|;
block|}
name|int
name|snapshotId
init|=
name|nextSnapshotId
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|File
name|file
init|=
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
name|replicationService
operator|.
name|getTempReplicationDir
argument_list|()
decl_stmt|;
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|snapshotFile
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"snapshot-"
operator|+
name|snapshotId
argument_list|)
expr_stmt|;
name|journalReplicatedFiles
operator|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
comment|// Store the list files associated with the snapshot.
name|ArrayList
argument_list|<
name|PBFileInfo
argument_list|>
name|snapshotInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|PBFileInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|DataFile
argument_list|>
name|journalFiles
init|=
name|store
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
decl_stmt|;
for|for
control|(
name|DataFile
name|df
range|:
name|journalFiles
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Look at what the slave has so that only the missing bits are transfered.
name|String
name|name
init|=
literal|"journal-"
operator|+
name|df
operator|.
name|getDataFileId
argument_list|()
decl_stmt|;
name|PBFileInfo
name|slaveInfo
init|=
name|slaveFiles
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// Use the checksum info to see if the slave has the file already.. Checksums are less acurrate for
comment|// small amounts of data.. so ignore small files.
if|if
condition|(
name|slaveInfo
operator|!=
literal|null
operator|&&
name|slaveInfo
operator|.
name|getEnd
argument_list|()
operator|>
literal|1024
operator|*
literal|512
condition|)
block|{
comment|// If the slave's file checksum matches what we have..
if|if
condition|(
name|ReplicationSupport
operator|.
name|checksum
argument_list|(
name|df
operator|.
name|getFile
argument_list|()
argument_list|,
literal|0
argument_list|,
name|slaveInfo
operator|.
name|getEnd
argument_list|()
argument_list|)
operator|==
name|slaveInfo
operator|.
name|getChecksum
argument_list|()
condition|)
block|{
comment|// is Our file longer? then we need to continue transferring the rest of the file.
if|if
condition|(
name|df
operator|.
name|getLength
argument_list|()
operator|>
name|slaveInfo
operator|.
name|getEnd
argument_list|()
condition|)
block|{
name|snapshotInfos
operator|.
name|add
argument_list|(
name|ReplicationSupport
operator|.
name|createInfo
argument_list|(
name|name
argument_list|,
name|df
operator|.
name|getFile
argument_list|()
argument_list|,
name|slaveInfo
operator|.
name|getEnd
argument_list|()
argument_list|,
name|df
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|journalReplicatedFiles
operator|.
name|add
argument_list|(
name|df
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
comment|// No need to replicate this file.
continue|continue;
block|}
block|}
block|}
comment|// If we got here then it means we need to transfer the whole file.
name|snapshotInfos
operator|.
name|add
argument_list|(
name|ReplicationSupport
operator|.
name|createInfo
argument_list|(
name|name
argument_list|,
name|df
operator|.
name|getFile
argument_list|()
argument_list|,
literal|0
argument_list|,
name|df
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|journalReplicatedFiles
operator|.
name|add
argument_list|(
name|df
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PBFileInfo
name|info
init|=
operator|new
name|PBFileInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setName
argument_list|(
literal|"database"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSnapshotId
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setStart
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|info
operator|.
name|setEnd
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setChecksum
argument_list|(
name|ReplicationSupport
operator|.
name|copyAndChecksum
argument_list|(
name|file
argument_list|,
name|snapshotFile
argument_list|)
argument_list|)
expr_stmt|;
name|snapshotInfos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|rcPayload
operator|.
name|setCopyFilesList
argument_list|(
name|snapshotInfos
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|deleteFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|slaveFiles
operator|.
name|remove
argument_list|(
literal|"database"
argument_list|)
expr_stmt|;
for|for
control|(
name|PBFileInfo
name|unused
range|:
name|slaveFiles
operator|.
name|values
argument_list|()
control|)
block|{
name|deleteFiles
operator|.
name|add
argument_list|(
name|unused
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rcPayload
operator|.
name|setDeleteFilesList
argument_list|(
name|deleteFiles
argument_list|)
expr_stmt|;
name|updateJournalReplicatedFiles
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|onFileTransfer
parameter_list|(
name|ReplicationFrame
name|frame
parameter_list|,
name|PBFileInfo
name|fileInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|replicationService
operator|.
name|getReplicationFile
argument_list|(
name|fileInfo
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|payloadSize
init|=
name|fileInfo
operator|.
name|getEnd
argument_list|()
operator|-
name|fileInfo
operator|.
name|getStart
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|<
name|fileInfo
operator|.
name|getStart
argument_list|()
operator|+
name|payloadSize
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Requested replication file dose not have enough data."
argument_list|)
throw|;
block|}
name|ReplicationFrame
name|rc
init|=
operator|new
name|ReplicationFrame
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setHeader
argument_list|(
operator|new
name|PBHeader
argument_list|()
operator|.
name|setType
argument_list|(
name|PBType
operator|.
name|FILE_TRANSFER_RESPONSE
argument_list|)
operator|.
name|setPayloadSize
argument_list|(
name|payloadSize
argument_list|)
argument_list|)
expr_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|rc
operator|.
name|setPayload
argument_list|(
name|is
argument_list|)
expr_stmt|;
try|try
block|{
name|is
operator|.
name|skip
argument_list|(
name|fileInfo
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{ 				}
block|}
block|}
block|}
comment|/** 	 * Looks at all the journal files being currently replicated and informs the KahaDB so that 	 * it does not delete them while the replication is occuring. 	 */
specifier|private
name|void
name|updateJournalReplicatedFiles
parameter_list|()
block|{
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|files
init|=
name|replicationService
operator|.
name|getStore
argument_list|()
operator|.
name|getJournalFilesBeingReplicated
argument_list|()
decl_stmt|;
name|files
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
name|sessionsSnapshot
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|sessions
init|)
block|{
comment|// Hurrah for copy on write..
name|sessionsSnapshot
operator|=
name|this
operator|.
name|sessions
expr_stmt|;
block|}
for|for
control|(
name|ReplicationSession
name|session
range|:
name|sessionsSnapshot
control|)
block|{
if|if
condition|(
name|session
operator|.
name|journalReplicatedFiles
operator|!=
literal|null
condition|)
block|{
name|files
operator|.
name|addAll
argument_list|(
name|session
operator|.
name|journalReplicatedFiles
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

