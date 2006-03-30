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
name|broker
operator|.
name|region
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|command
operator|.
name|MessageDispatch
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

begin_class
specifier|public
class|class
name|QueueBrowserSubscription
extends|extends
name|QueueSubscription
block|{
name|boolean
name|browseDone
decl_stmt|;
specifier|public
name|QueueBrowserSubscription
parameter_list|(
name|Broker
name|broker
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
name|IndirectMessageReference
operator|)
name|node
operator|)
operator|.
name|isAcked
argument_list|()
return|;
block|}
specifier|public
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
name|this
operator|.
name|pending
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|void
name|browseDone
parameter_list|()
throws|throws
name|Exception
block|{
name|browseDone
operator|=
literal|true
expr_stmt|;
name|add
argument_list|(
name|IndirectMessageReference
operator|.
name|END_OF_BROWSE_MARKER
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageDispatch
name|createMessageDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
name|IndirectMessageReference
operator|.
name|END_OF_BROWSE_MARKER
condition|)
block|{
name|MessageDispatch
name|md
init|=
operator|new
name|MessageDispatch
argument_list|()
decl_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|md
operator|.
name|setConsumerId
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|md
operator|.
name|setDestination
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|md
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createMessageDispatch
argument_list|(
name|node
argument_list|,
name|message
argument_list|)
return|;
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
block|}
end_class

end_unit

