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
name|broker
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|ConnectionId
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
name|ConnectionInfo
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
name|TransactionId
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
name|WireFormatInfo
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
name|XATransactionId
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
name|filter
operator|.
name|MessageEvaluationContext
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
name|security
operator|.
name|MessageAuthorizationPolicy
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
name|security
operator|.
name|SecurityContext
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
name|state
operator|.
name|ConnectionState
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
name|transaction
operator|.
name|Transaction
import|;
end_import

begin_comment
comment|/**  * Used to hold context information needed to process requests sent to a broker.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionContext
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Connector
name|connector
decl_stmt|;
specifier|private
name|Broker
name|broker
decl_stmt|;
specifier|private
name|boolean
name|inRecoveryMode
decl_stmt|;
specifier|private
name|Transaction
name|transaction
decl_stmt|;
specifier|private
name|ConcurrentMap
argument_list|<
name|TransactionId
argument_list|,
name|Transaction
argument_list|>
name|transactions
decl_stmt|;
specifier|private
name|SecurityContext
name|securityContext
decl_stmt|;
specifier|private
name|ConnectionId
name|connectionId
decl_stmt|;
specifier|private
name|String
name|clientId
decl_stmt|;
specifier|private
name|String
name|userName
decl_stmt|;
specifier|private
name|boolean
name|reconnect
decl_stmt|;
specifier|private
name|WireFormatInfo
name|wireFormatInfo
decl_stmt|;
specifier|private
name|Object
name|longTermStoreContext
decl_stmt|;
specifier|private
name|boolean
name|producerFlowControl
init|=
literal|true
decl_stmt|;
specifier|private
name|MessageAuthorizationPolicy
name|messageAuthorizationPolicy
decl_stmt|;
specifier|private
name|boolean
name|networkConnection
decl_stmt|;
specifier|private
name|boolean
name|faultTolerant
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|stopping
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MessageEvaluationContext
name|messageEvaluationContext
decl_stmt|;
specifier|private
name|boolean
name|dontSendReponse
decl_stmt|;
specifier|private
name|boolean
name|clientMaster
init|=
literal|true
decl_stmt|;
specifier|private
name|ConnectionState
name|connectionState
decl_stmt|;
specifier|private
name|XATransactionId
name|xid
decl_stmt|;
specifier|public
name|ConnectionContext
parameter_list|()
block|{
name|this
operator|.
name|messageEvaluationContext
operator|=
operator|new
name|MessageEvaluationContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionContext
parameter_list|(
name|MessageEvaluationContext
name|messageEvaluationContext
parameter_list|)
block|{
name|this
operator|.
name|messageEvaluationContext
operator|=
name|messageEvaluationContext
expr_stmt|;
block|}
specifier|public
name|ConnectionContext
parameter_list|(
name|ConnectionInfo
name|info
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setClientId
argument_list|(
name|info
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|setUserName
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|setConnectionId
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionContext
name|copy
parameter_list|()
block|{
name|ConnectionContext
name|rc
init|=
operator|new
name|ConnectionContext
argument_list|(
name|this
operator|.
name|messageEvaluationContext
argument_list|)
decl_stmt|;
name|rc
operator|.
name|connection
operator|=
name|this
operator|.
name|connection
expr_stmt|;
name|rc
operator|.
name|connector
operator|=
name|this
operator|.
name|connector
expr_stmt|;
name|rc
operator|.
name|broker
operator|=
name|this
operator|.
name|broker
expr_stmt|;
name|rc
operator|.
name|inRecoveryMode
operator|=
name|this
operator|.
name|inRecoveryMode
expr_stmt|;
name|rc
operator|.
name|transaction
operator|=
name|this
operator|.
name|transaction
expr_stmt|;
name|rc
operator|.
name|transactions
operator|=
name|this
operator|.
name|transactions
expr_stmt|;
name|rc
operator|.
name|securityContext
operator|=
name|this
operator|.
name|securityContext
expr_stmt|;
name|rc
operator|.
name|connectionId
operator|=
name|this
operator|.
name|connectionId
expr_stmt|;
name|rc
operator|.
name|clientId
operator|=
name|this
operator|.
name|clientId
expr_stmt|;
name|rc
operator|.
name|userName
operator|=
name|this
operator|.
name|userName
expr_stmt|;
name|rc
operator|.
name|reconnect
operator|=
name|this
operator|.
name|reconnect
expr_stmt|;
name|rc
operator|.
name|wireFormatInfo
operator|=
name|this
operator|.
name|wireFormatInfo
expr_stmt|;
name|rc
operator|.
name|longTermStoreContext
operator|=
name|this
operator|.
name|longTermStoreContext
expr_stmt|;
name|rc
operator|.
name|producerFlowControl
operator|=
name|this
operator|.
name|producerFlowControl
expr_stmt|;
name|rc
operator|.
name|messageAuthorizationPolicy
operator|=
name|this
operator|.
name|messageAuthorizationPolicy
expr_stmt|;
name|rc
operator|.
name|networkConnection
operator|=
name|this
operator|.
name|networkConnection
expr_stmt|;
name|rc
operator|.
name|faultTolerant
operator|=
name|this
operator|.
name|faultTolerant
expr_stmt|;
name|rc
operator|.
name|stopping
operator|.
name|set
argument_list|(
name|this
operator|.
name|stopping
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|dontSendReponse
operator|=
name|this
operator|.
name|dontSendReponse
expr_stmt|;
name|rc
operator|.
name|clientMaster
operator|=
name|this
operator|.
name|clientMaster
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|SecurityContext
name|getSecurityContext
parameter_list|()
block|{
return|return
name|securityContext
return|;
block|}
specifier|public
name|void
name|setSecurityContext
parameter_list|(
name|SecurityContext
name|subject
parameter_list|)
block|{
name|this
operator|.
name|securityContext
operator|=
name|subject
expr_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|setUserName
argument_list|(
name|subject
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setUserName
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the broker being used.      */
specifier|public
name|Broker
name|getBroker
parameter_list|()
block|{
return|return
name|broker
return|;
block|}
comment|/**      * @param broker being used      */
specifier|public
name|void
name|setBroker
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
comment|/**      * @return the connection being used      */
specifier|public
name|Connection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
comment|/**      * @param connection being used      */
specifier|public
name|void
name|setConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
comment|/**      * @return the transaction being used.      */
specifier|public
name|Transaction
name|getTransaction
parameter_list|()
block|{
return|return
name|transaction
return|;
block|}
comment|/**      * @param transaction being used.      */
specifier|public
name|void
name|setTransaction
parameter_list|(
name|Transaction
name|transaction
parameter_list|)
block|{
name|this
operator|.
name|transaction
operator|=
name|transaction
expr_stmt|;
block|}
comment|/**      * @return the connector being used.      */
specifier|public
name|Connector
name|getConnector
parameter_list|()
block|{
return|return
name|connector
return|;
block|}
comment|/**      * @param connector being used.      */
specifier|public
name|void
name|setConnector
parameter_list|(
name|Connector
name|connector
parameter_list|)
block|{
name|this
operator|.
name|connector
operator|=
name|connector
expr_stmt|;
block|}
specifier|public
name|MessageAuthorizationPolicy
name|getMessageAuthorizationPolicy
parameter_list|()
block|{
return|return
name|messageAuthorizationPolicy
return|;
block|}
comment|/**      * Sets the policy used to decide if the current connection is authorized to      * consume a given message      */
specifier|public
name|void
name|setMessageAuthorizationPolicy
parameter_list|(
name|MessageAuthorizationPolicy
name|messageAuthorizationPolicy
parameter_list|)
block|{
name|this
operator|.
name|messageAuthorizationPolicy
operator|=
name|messageAuthorizationPolicy
expr_stmt|;
block|}
comment|/**      * @return      */
specifier|public
name|boolean
name|isInRecoveryMode
parameter_list|()
block|{
return|return
name|inRecoveryMode
return|;
block|}
specifier|public
name|void
name|setInRecoveryMode
parameter_list|(
name|boolean
name|inRecoveryMode
parameter_list|)
block|{
name|this
operator|.
name|inRecoveryMode
operator|=
name|inRecoveryMode
expr_stmt|;
block|}
specifier|public
name|ConcurrentMap
argument_list|<
name|TransactionId
argument_list|,
name|Transaction
argument_list|>
name|getTransactions
parameter_list|()
block|{
return|return
name|transactions
return|;
block|}
specifier|public
name|void
name|setTransactions
parameter_list|(
name|ConcurrentMap
argument_list|<
name|TransactionId
argument_list|,
name|Transaction
argument_list|>
name|transactions
parameter_list|)
block|{
name|this
operator|.
name|transactions
operator|=
name|transactions
expr_stmt|;
block|}
specifier|public
name|boolean
name|isInTransaction
parameter_list|()
block|{
return|return
name|transaction
operator|!=
literal|null
return|;
block|}
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
specifier|public
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReconnect
parameter_list|()
block|{
return|return
name|reconnect
return|;
block|}
specifier|public
name|void
name|setReconnect
parameter_list|(
name|boolean
name|reconnect
parameter_list|)
block|{
name|this
operator|.
name|reconnect
operator|=
name|reconnect
expr_stmt|;
block|}
specifier|public
name|WireFormatInfo
name|getWireFormatInfo
parameter_list|()
block|{
return|return
name|wireFormatInfo
return|;
block|}
specifier|public
name|void
name|setWireFormatInfo
parameter_list|(
name|WireFormatInfo
name|wireFormatInfo
parameter_list|)
block|{
name|this
operator|.
name|wireFormatInfo
operator|=
name|wireFormatInfo
expr_stmt|;
block|}
specifier|public
name|ConnectionId
name|getConnectionId
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
specifier|public
name|void
name|setConnectionId
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|protected
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
specifier|public
name|MessageEvaluationContext
name|getMessageEvaluationContext
parameter_list|()
block|{
return|return
name|messageEvaluationContext
return|;
block|}
specifier|public
name|Object
name|getLongTermStoreContext
parameter_list|()
block|{
return|return
name|longTermStoreContext
return|;
block|}
specifier|public
name|void
name|setLongTermStoreContext
parameter_list|(
name|Object
name|longTermStoreContext
parameter_list|)
block|{
name|this
operator|.
name|longTermStoreContext
operator|=
name|longTermStoreContext
expr_stmt|;
block|}
specifier|public
name|boolean
name|isProducerFlowControl
parameter_list|()
block|{
return|return
name|producerFlowControl
return|;
block|}
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|disableProducerFlowControl
parameter_list|)
block|{
name|this
operator|.
name|producerFlowControl
operator|=
name|disableProducerFlowControl
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAllowedToConsume
parameter_list|(
name|MessageReference
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|messageAuthorizationPolicy
operator|!=
literal|null
condition|)
block|{
return|return
name|messageAuthorizationPolicy
operator|.
name|isAllowedToConsume
argument_list|(
name|this
argument_list|,
name|n
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isNetworkConnection
parameter_list|()
block|{
return|return
name|networkConnection
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setNetworkConnection
parameter_list|(
name|boolean
name|networkConnection
parameter_list|)
block|{
name|this
operator|.
name|networkConnection
operator|=
name|networkConnection
expr_stmt|;
block|}
specifier|public
name|AtomicBoolean
name|getStopping
parameter_list|()
block|{
return|return
name|stopping
return|;
block|}
specifier|public
name|void
name|setDontSendReponse
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|this
operator|.
name|dontSendReponse
operator|=
name|b
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDontSendReponse
parameter_list|()
block|{
return|return
name|dontSendReponse
return|;
block|}
comment|/**      * @return the clientMaster      */
specifier|public
name|boolean
name|isClientMaster
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientMaster
return|;
block|}
comment|/**      * @param clientMaster the clientMaster to set      */
specifier|public
name|void
name|setClientMaster
parameter_list|(
name|boolean
name|clientMaster
parameter_list|)
block|{
name|this
operator|.
name|clientMaster
operator|=
name|clientMaster
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
name|faultTolerant
return|;
block|}
specifier|public
name|void
name|setFaultTolerant
parameter_list|(
name|boolean
name|faultTolerant
parameter_list|)
block|{
name|this
operator|.
name|faultTolerant
operator|=
name|faultTolerant
expr_stmt|;
block|}
specifier|public
name|void
name|setConnectionState
parameter_list|(
name|ConnectionState
name|connectionState
parameter_list|)
block|{
name|this
operator|.
name|connectionState
operator|=
name|connectionState
expr_stmt|;
block|}
specifier|public
name|ConnectionState
name|getConnectionState
parameter_list|()
block|{
return|return
name|this
operator|.
name|connectionState
return|;
block|}
specifier|public
name|void
name|setXid
parameter_list|(
name|XATransactionId
name|id
parameter_list|)
block|{
name|this
operator|.
name|xid
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|XATransactionId
name|getXid
parameter_list|()
block|{
return|return
name|xid
return|;
block|}
specifier|public
name|boolean
name|isAllowLinkStealing
parameter_list|()
block|{
return|return
name|connector
operator|!=
literal|null
operator|&&
name|connector
operator|.
name|isAllowLinkStealing
argument_list|()
return|;
block|}
block|}
end_class

end_unit

