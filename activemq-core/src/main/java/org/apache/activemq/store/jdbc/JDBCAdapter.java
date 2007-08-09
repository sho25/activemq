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
name|SQLException
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
name|MessageId
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
name|SubscriptionInfo
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|JDBCAdapter
block|{
name|void
name|setStatements
parameter_list|(
name|Statements
name|statementProvider
parameter_list|)
function_decl|;
name|void
name|doCreateTables
parameter_list|(
name|TransactionContext
name|c
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doDropTables
parameter_list|(
name|TransactionContext
name|c
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doAddMessage
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|MessageId
name|messageID
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|long
name|expiration
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doAddMessageReference
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|expirationTime
parameter_list|,
name|String
name|messageRef
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|byte
index|[]
name|doGetMessage
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|long
name|seq
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|String
name|doGetMessageReference
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|long
name|id
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doRemoveMessage
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|long
name|seq
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doRecover
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|JDBCMessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|doSetLastAck
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|long
name|seq
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doRecoverSubscription
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|JDBCMessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|doRecoverNextMessages
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|long
name|seq
parameter_list|,
name|int
name|maxReturned
parameter_list|,
name|JDBCMessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|doSetSubscriberEntry
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|SubscriptionInfo
name|subscriptionInfo
parameter_list|,
name|boolean
name|retroactive
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|SubscriptionInfo
name|doGetSubscriberEntry
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|long
name|getBrokerSequenceId
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|MessageId
name|messageID
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doRemoveAllMessages
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destinationName
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doDeleteSubscription
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destinationName
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doDeleteOldMessages
parameter_list|(
name|TransactionContext
name|c
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|long
name|doGetLastMessageBrokerSequenceId
parameter_list|(
name|TransactionContext
name|c
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|Set
name|doGetDestinations
parameter_list|(
name|TransactionContext
name|c
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|setUseExternalMessageReferences
parameter_list|(
name|boolean
name|useExternalMessageReferences
parameter_list|)
function_decl|;
name|SubscriptionInfo
index|[]
name|doGetAllSubscriptions
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|int
name|doGetDurableSubscriberMessageCount
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|int
name|doGetMessageCount
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
name|void
name|doRecoverNextMessages
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|nextSeq
parameter_list|,
name|int
name|maxReturned
parameter_list|,
name|JDBCMessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|long
name|doGetLastAckedDurableSubscriberMessageId
parameter_list|(
name|TransactionContext
name|c
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

