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
name|web
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|MessageAvailableListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|continuation
operator|.
name|Continuation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/*  * Listen for available messages and wakeup any continuations.  */
end_comment

begin_class
specifier|public
class|class
name|AjaxListener
implements|implements
name|MessageAvailableListener
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AjaxListener
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|maximumReadTimeout
decl_stmt|;
specifier|private
specifier|final
name|AjaxWebClient
name|client
decl_stmt|;
specifier|private
name|long
name|lastAccess
decl_stmt|;
specifier|private
name|Continuation
name|continuation
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|UndeliveredAjaxMessage
argument_list|>
name|undeliveredMessages
init|=
operator|new
name|LinkedList
argument_list|<
name|UndeliveredAjaxMessage
argument_list|>
argument_list|()
decl_stmt|;
name|AjaxListener
parameter_list|(
name|AjaxWebClient
name|client
parameter_list|,
name|long
name|maximumReadTimeout
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|maximumReadTimeout
operator|=
name|maximumReadTimeout
expr_stmt|;
name|access
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|access
parameter_list|()
block|{
name|lastAccess
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|setContinuation
parameter_list|(
name|Continuation
name|continuation
parameter_list|)
block|{
name|this
operator|.
name|continuation
operator|=
name|continuation
expr_stmt|;
block|}
specifier|public
name|LinkedList
argument_list|<
name|UndeliveredAjaxMessage
argument_list|>
name|getUndeliveredMessages
parameter_list|()
block|{
return|return
name|undeliveredMessages
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|onMessageAvailable
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"message for "
operator|+
name|consumer
operator|+
literal|" continuation="
operator|+
name|continuation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|continuation
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"message is "
operator|+
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|continuation
operator|.
name|isResumed
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resuming suspended continuation "
operator|+
name|continuation
argument_list|)
expr_stmt|;
name|continuation
operator|.
name|setAttribute
argument_list|(
literal|"undelivered_message"
argument_list|,
operator|new
name|UndeliveredAjaxMessage
argument_list|(
name|message
argument_list|,
name|consumer
argument_list|)
argument_list|)
expr_stmt|;
name|continuation
operator|.
name|resume
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Message available, but continuation is already resumed.  Buffer for next time."
argument_list|)
expr_stmt|;
name|bufferMessageForDelivery
argument_list|(
name|message
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
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
name|error
argument_list|(
literal|"Error receiving message "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastAccess
operator|>
literal|2
operator|*
name|this
operator|.
name|maximumReadTimeout
condition|)
block|{
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|client
operator|.
name|closeConsumers
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|bufferMessageForDelivery
argument_list|(
name|message
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error receiving message "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|bufferMessageForDelivery
parameter_list|(
name|Message
name|message
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|)
block|{
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|undeliveredMessages
init|)
block|{
name|undeliveredMessages
operator|.
name|addLast
argument_list|(
operator|new
name|UndeliveredAjaxMessage
argument_list|(
name|message
argument_list|,
name|consumer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

