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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|Broker
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
name|ConsumerInfo
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
name|MessageAck
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_class
specifier|public
class|class
name|QueueBrowserSubscription
extends|extends
name|QueueSubscription
block|{
name|int
name|queueRefs
decl_stmt|;
name|boolean
name|browseDone
decl_stmt|;
name|boolean
name|destinationsAdded
decl_stmt|;
specifier|public
name|QueueBrowserSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|SystemUsage
name|usageManager
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
return|return
operator|!
operator|(
operator|(
name|QueueMessageReference
operator|)
name|node
operator|)
operator|.
name|isAcked
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"QueueBrowserSubscription:"
operator|+
literal|" consumer="
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|", destinations="
operator|+
name|destinations
operator|.
name|size
argument_list|()
operator|+
literal|", dispatched="
operator|+
name|dispatched
operator|.
name|size
argument_list|()
operator|+
literal|", delivered="
operator|+
name|this
operator|.
name|prefetchExtension
operator|+
literal|", pending="
operator|+
name|getPendingQueueSize
argument_list|()
return|;
block|}
specifier|synchronized
specifier|public
name|void
name|destinationsAdded
parameter_list|()
throws|throws
name|Exception
block|{
name|destinationsAdded
operator|=
literal|true
expr_stmt|;
name|checkDone
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|checkDone
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|browseDone
operator|&&
name|queueRefs
operator|==
literal|0
operator|&&
name|destinationsAdded
condition|)
block|{
name|browseDone
operator|=
literal|true
expr_stmt|;
name|add
argument_list|(
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|!
name|browseDone
operator|&&
name|super
operator|.
name|matches
argument_list|(
name|node
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**      * Since we are a browser we don't really remove the message from the queue.      */
specifier|protected
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|,
specifier|final
name|MessageReference
name|n
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|synchronized
specifier|public
name|void
name|incrementQueueRef
parameter_list|()
block|{
name|queueRefs
operator|++
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|decrementQueueRef
parameter_list|()
throws|throws
name|Exception
block|{
name|queueRefs
operator|--
expr_stmt|;
name|checkDone
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

