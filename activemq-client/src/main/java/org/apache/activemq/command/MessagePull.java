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
name|command
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
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * Used to pull messages on demand.  *  * @openwire:marshaller code="20"  *  *  */
end_comment

begin_class
specifier|public
class|class
name|MessagePull
extends|extends
name|BaseCommand
implements|implements
name|TransientInitializer
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|MESSAGE_PULL
decl_stmt|;
specifier|protected
name|ConsumerId
name|consumerId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|long
name|timeout
decl_stmt|;
specifier|private
name|MessageId
name|messageId
decl_stmt|;
specifier|private
name|String
name|correlationId
decl_stmt|;
specifier|private
specifier|transient
name|int
name|quantity
init|=
literal|1
decl_stmt|;
specifier|private
specifier|transient
name|boolean
name|alwaysSignalDone
decl_stmt|;
specifier|private
specifier|transient
name|boolean
name|tracked
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processMessagePull
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Configures a message pull from the consumer information      */
specifier|public
name|void
name|configure
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
name|setConsumerId
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|setDestination
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ConsumerId
name|getConsumerId
parameter_list|()
block|{
return|return
name|consumerId
return|;
block|}
specifier|public
name|void
name|setConsumerId
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
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
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
comment|/**      * An optional correlation ID which could be used by a broker to decide which messages are pulled      * on demand from a queue for a consumer      *      * @openwire:property version=3      */
specifier|public
name|String
name|getCorrelationId
parameter_list|()
block|{
return|return
name|correlationId
return|;
block|}
specifier|public
name|void
name|setCorrelationId
parameter_list|(
name|String
name|correlationId
parameter_list|)
block|{
name|this
operator|.
name|correlationId
operator|=
name|correlationId
expr_stmt|;
block|}
comment|/**      * An optional message ID which could be used by a broker to decide which messages are pulled      * on demand from a queue for a consumer      *      * @openwire:property version=3      */
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
specifier|public
name|void
name|setTracked
parameter_list|(
name|boolean
name|tracked
parameter_list|)
block|{
name|this
operator|.
name|tracked
operator|=
name|tracked
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTracked
parameter_list|()
block|{
return|return
name|this
operator|.
name|tracked
return|;
block|}
specifier|public
name|int
name|getQuantity
parameter_list|()
block|{
return|return
name|quantity
return|;
block|}
specifier|public
name|void
name|setQuantity
parameter_list|(
name|int
name|quantity
parameter_list|)
block|{
name|this
operator|.
name|quantity
operator|=
name|quantity
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAlwaysSignalDone
parameter_list|()
block|{
return|return
name|alwaysSignalDone
return|;
block|}
specifier|public
name|void
name|setAlwaysSignalDone
parameter_list|(
name|boolean
name|alwaysSignalDone
parameter_list|)
block|{
name|this
operator|.
name|alwaysSignalDone
operator|=
name|alwaysSignalDone
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initTransients
parameter_list|()
block|{
name|quantity
operator|=
literal|1
expr_stmt|;
name|alwaysSignalDone
operator|=
literal|false
expr_stmt|;
name|tracked
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

