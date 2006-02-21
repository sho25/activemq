begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_comment
comment|/**  *   * @openwire:marshaller code="50"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JournalTopicAck
extends|extends
name|DataStructureSupport
implements|implements
name|DataStructure
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|JOURNAL_ACK
decl_stmt|;
name|ActiveMQDestination
name|destination
decl_stmt|;
name|String
name|clientId
decl_stmt|;
name|String
name|subscritionName
decl_stmt|;
name|MessageId
name|messageId
decl_stmt|;
name|long
name|messageSequenceId
decl_stmt|;
name|TransactionId
name|transactionId
decl_stmt|;
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|MessageId
name|getMessageId
parameter_list|()
block|{
return|return
name|messageId
return|;
block|}
specifier|public
name|void
name|setMessageId
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getMessageSequenceId
parameter_list|()
block|{
return|return
name|messageSequenceId
return|;
block|}
specifier|public
name|void
name|setMessageSequenceId
parameter_list|(
name|long
name|messageSequenceId
parameter_list|)
block|{
name|this
operator|.
name|messageSequenceId
operator|=
name|messageSequenceId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getSubscritionName
parameter_list|()
block|{
return|return
name|subscritionName
return|;
block|}
specifier|public
name|void
name|setSubscritionName
parameter_list|(
name|String
name|subscritionName
parameter_list|)
block|{
name|this
operator|.
name|subscritionName
operator|=
name|subscritionName
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|TransactionId
name|getTransactionId
parameter_list|()
block|{
return|return
name|transactionId
return|;
block|}
specifier|public
name|void
name|setTransactionId
parameter_list|(
name|TransactionId
name|transaction
parameter_list|)
block|{
name|this
operator|.
name|transactionId
operator|=
name|transaction
expr_stmt|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

