begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Adapter to the actual persistence mechanism used with ActiveMQ  *  * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|PersistenceAdapter
extends|extends
name|Service
block|{
comment|/**      * Returns a set of all the {@link org.apache.activemq.command.ActiveMQDestination}      * objects that the persistence store is aware exist.      *      * @return      */
specifier|public
name|Set
name|getDestinations
parameter_list|()
function_decl|;
comment|/**      * Factory method to create a new queue message store with the given destination name      */
specifier|public
name|MessageStore
name|createQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Factory method to create a new topic message store with the given destination name      */
specifier|public
name|TopicMessageStore
name|createTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Factory method to create a new persistent prepared transaction store for XA recovery      */
specifier|public
name|TransactionStore
name|createTransactionStore
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * This method starts a transaction on the persistent storage - which is nothing to      * do with JMS or XA transactions - its purely a mechanism to perform multiple writes      * to a persistent store in 1 transaction as a performance optimization.      *<p/>      * Typically one transaction will require one disk synchronization point and so for      * real high performance its usually faster to perform many writes within the same      * transaction to minimize latency caused by disk synchronization. This is especially      * true when using tools like Berkeley Db or embedded JDBC servers.      */
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Commit a persistence transaction      *      * @see PersistenceAdapter#beginTransaction()      */
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Rollback a persistence transaction      *      * @see PersistenceAdapter#beginTransaction()      */
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      *       * @return      * @throws IOException      */
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Delete's all the messages in the persistent store.      *       * @throws IOException      */
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|boolean
name|isUseExternalMessageReferences
parameter_list|()
function_decl|;
specifier|public
name|void
name|setUseExternalMessageReferences
parameter_list|(
name|boolean
name|enable
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

