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
name|spring
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|core
operator|.
name|JmsTemplate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_class
specifier|public
class|class
name|SpringConsumer
extends|extends
name|ConsumerBean
implements|implements
name|MessageListener
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SpringConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|JmsTemplate
name|template
decl_stmt|;
specifier|private
name|String
name|myId
init|=
literal|"foo"
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
name|String
name|selector
init|=
literal|"next = '"
operator|+
name|myId
operator|+
literal|"'"
decl_stmt|;
try|try
block|{
name|ConnectionFactory
name|factory
init|=
name|template
operator|.
name|getConnectionFactory
argument_list|()
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
comment|// we might be a reusable connection in spring
comment|// so lets only set the client ID once if its not set
synchronized|synchronized
init|(
name|connection
init|)
block|{
if|if
condition|(
name|connection
operator|.
name|getClientID
argument_list|()
operator|==
literal|null
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|myId
argument_list|)
expr_stmt|;
block|}
block|}
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|selector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|super
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to acknowledge: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Properties
comment|//-------------------------------------------------------------------------
specifier|public
name|Destination
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
name|Destination
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
specifier|public
name|String
name|getMyId
parameter_list|()
block|{
return|return
name|myId
return|;
block|}
specifier|public
name|void
name|setMyId
parameter_list|(
name|String
name|myId
parameter_list|)
block|{
name|this
operator|.
name|myId
operator|=
name|myId
expr_stmt|;
block|}
specifier|public
name|JmsTemplate
name|getTemplate
parameter_list|()
block|{
return|return
name|template
return|;
block|}
specifier|public
name|void
name|setTemplate
parameter_list|(
name|JmsTemplate
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
block|}
end_class

end_unit

