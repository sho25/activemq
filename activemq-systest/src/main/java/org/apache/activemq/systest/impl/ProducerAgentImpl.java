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
name|systest
operator|.
name|impl
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
name|systest
operator|.
name|AgentStopper
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
name|systest
operator|.
name|MessageList
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
name|systest
operator|.
name|ProducerAgent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
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
name|MessageProducer
import|;
end_import

begin_comment
comment|/**  * A simple in JVM implementation of a {@link ProducerAgent}  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ProducerAgentImpl
extends|extends
name|JmsClientSupport
implements|implements
name|ProducerAgent
block|{
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|boolean
name|persistent
init|=
literal|true
decl_stmt|;
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|producer
operator|=
name|createProducer
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|MessageList
name|messageList
parameter_list|)
throws|throws
name|JMSException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"About to send: "
operator|+
name|messageList
operator|.
name|getSize
argument_list|()
operator|+
literal|" message(s) to destination: "
operator|+
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|messageList
operator|.
name|sendMessages
argument_list|(
name|getSession
argument_list|()
argument_list|,
name|producer
argument_list|)
expr_stmt|;
if|if
condition|(
name|isTransacted
argument_list|()
condition|)
block|{
name|getSession
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent: "
operator|+
name|messageList
operator|.
name|getSize
argument_list|()
operator|+
literal|" message(s) to destination: "
operator|+
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|MessageList
name|messageList
parameter_list|,
name|int
name|percent
parameter_list|)
throws|throws
name|JMSException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"About to send: "
operator|+
name|percent
operator|+
literal|" % of the message(s) to destination: "
operator|+
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|messageList
operator|.
name|sendMessages
argument_list|(
name|getSession
argument_list|()
argument_list|,
name|producer
argument_list|,
name|percent
argument_list|)
expr_stmt|;
if|if
condition|(
name|isTransacted
argument_list|()
condition|)
block|{
name|getSession
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent: "
operator|+
name|percent
operator|+
literal|" % of the message(s) to destination: "
operator|+
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
specifier|public
name|void
name|setPersistent
parameter_list|(
name|boolean
name|persistent
parameter_list|)
block|{
name|this
operator|.
name|persistent
operator|=
name|persistent
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|(
name|AgentStopper
name|stopper
parameter_list|)
block|{
if|if
condition|(
name|producer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|stopper
operator|.
name|onException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|producer
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|super
operator|.
name|stop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|MessageProducer
name|createProducer
parameter_list|()
throws|throws
name|JMSException
block|{
name|MessageProducer
name|answer
init|=
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|answer
operator|.
name|setDeliveryMode
argument_list|(
name|isPersistent
argument_list|()
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

