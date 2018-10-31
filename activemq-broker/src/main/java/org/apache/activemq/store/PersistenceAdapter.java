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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|broker
operator|.
name|ConnectionContext
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
name|broker
operator|.
name|scheduler
operator|.
name|JobSchedulerStore
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
name|command
operator|.
name|ActiveMQDestination
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
name|command
operator|.
name|ActiveMQQueue
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
name|command
operator|.
name|ActiveMQTopic
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
name|command
operator|.
name|ProducerId
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_comment
comment|/**  * Adapter to the actual persistence mechanism used with ActiveMQ  *  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|PersistenceAdapter
extends|extends
name|Service
block|{
comment|/**      * Returns a set of all the      * {@link org.apache.activemq.command.ActiveMQDestination} objects that the      * persistence store is aware exist.      *      * @return active destinations      */
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDestinations
parameter_list|()
function_decl|;
comment|/**      * Factory method to create a new queue message store with the given      * destination name      *      * @param destination      * @return the message store      * @throws IOException      */
name|MessageStore
name|createQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Factory method to create a new topic message store with the given      * destination name      *      * @param destination      * @return the topic message store      * @throws IOException      */
name|TopicMessageStore
name|createTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates and returns a new Job Scheduler store instance.      *      * @return a new JobSchedulerStore instance if this Persistence adapter provides its own.      *      * @throws IOException If an error occurs while creating the new JobSchedulerStore.      * @throws UnsupportedOperationException If this adapter does not provide its own      *                                       scheduler store implementation.      */
name|JobSchedulerStore
name|createJobSchedulerStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
function_decl|;
comment|/**      * Cleanup method to remove any state associated with the given destination.      * This method does not stop the message store (it might not be cached).      *      * @param destination      *            Destination to forget      */
name|void
name|removeQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
function_decl|;
comment|/**      * Cleanup method to remove any state associated with the given destination      * This method does not stop the message store (it might not be cached).      *      * @param destination      *            Destination to forget      */
name|void
name|removeTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
function_decl|;
comment|/**      * Factory method to create a new persistent prepared transaction store for      * XA recovery      *      * @return transaction store      * @throws IOException      */
name|TransactionStore
name|createTransactionStore
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * This method starts a transaction on the persistent storage - which is      * nothing to do with JMS or XA transactions - its purely a mechanism to      * perform multiple writes to a persistent store in 1 transaction as a      * performance optimization.      *<p/>      * Typically one transaction will require one disk synchronization point and      * so for real high performance its usually faster to perform many writes      * within the same transaction to minimize latency caused by disk      * synchronization. This is especially true when using tools like Berkeley      * Db or embedded JDBC servers.      *      * @param context      * @throws IOException      */
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Commit a persistence transaction      *      * @param context      * @throws IOException      *      * @see PersistenceAdapter#beginTransaction(ConnectionContext context)      */
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Rollback a persistence transaction      *      * @param context      * @throws IOException      *      * @see PersistenceAdapter#beginTransaction(ConnectionContext context)      */
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      *      * @return last broker sequence      * @throws IOException      */
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Delete's all the messages in the persistent store.      *      * @throws IOException      */
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * @param usageManager      *            The UsageManager that is controlling the broker's memory      *            usage.      */
name|void
name|setUsageManager
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
function_decl|;
comment|/**      * Set the name of the broker using the adapter      *      * @param brokerName      */
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
function_decl|;
comment|/**      * Set the directory where any data files should be created      *      * @param dir      */
name|void
name|setDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
function_decl|;
comment|/**      * @return the directory used by the persistence adaptor      */
name|File
name|getDirectory
parameter_list|()
function_decl|;
comment|/**      * checkpoint any      *      * @param cleanup      * @throws IOException      *      */
name|void
name|checkpoint
parameter_list|(
name|boolean
name|cleanup
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * A hint to return the size of the store on disk      *      * @return disk space used in bytes of 0 if not implemented      */
name|long
name|size
parameter_list|()
function_decl|;
comment|/**      * return the last stored producer sequenceId for this producer Id used to      * suppress duplicate sends on failover reconnect at the transport when a      * reconnect occurs      *      * @param id      *            the producerId to find a sequenceId for      * @return the last stored sequence id or -1 if no suppression needed      */
name|long
name|getLastProducerSequenceId
parameter_list|(
name|ProducerId
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|void
name|allowIOResumption
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

