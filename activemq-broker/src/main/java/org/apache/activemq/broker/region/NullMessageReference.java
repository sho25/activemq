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
operator|.
name|region
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
name|command
operator|.
name|ActiveMQMessage
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
name|ConsumerId
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
name|Message
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

begin_comment
comment|/**  * Used by the {@link QueueMessageReference#NULL_MESSAGE}  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NullMessageReference
implements|implements
name|QueueMessageReference
block|{
specifier|private
specifier|final
name|ActiveMQMessage
name|message
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|references
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|drop
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|dropIfLive
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|LockOwner
name|getLockOwner
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAcked
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDropped
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|lock
parameter_list|(
name|LockOwner
name|subscription
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAcked
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|unlock
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|decrementReferenceCount
parameter_list|()
block|{
return|return
operator|--
name|references
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getExpiration
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getGroupID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getGroupSequence
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|getMessageHardRef
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|MessageId
name|getMessageId
parameter_list|()
block|{
return|return
name|message
operator|.
name|getMessageId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRedeliveryCounter
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
return|return
name|references
return|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|getRegionDestination
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSize
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|ConsumerId
name|getTargetConsumerId
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|incrementRedeliveryCounter
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|incrementReferenceCount
parameter_list|()
block|{
return|return
operator|++
name|references
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExpired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAdvisory
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canProcessAsExpired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

