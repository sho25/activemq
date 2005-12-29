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
name|perf
package|;
end_package

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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|PerfConsumer
implements|implements
name|MessageListener
block|{
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|protected
name|PerfRate
name|rate
init|=
operator|new
name|PerfRate
argument_list|()
decl_stmt|;
specifier|public
name|PerfConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|String
name|consumerName
parameter_list|)
throws|throws
name|JMSException
block|{
name|connection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|instanceof
name|Topic
operator|&&
name|consumerName
operator|!=
literal|null
operator|&&
name|consumerName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|consumer
operator|=
name|s
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|consumerName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|s
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PerfConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|rate
operator|.
name|getRate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|PerfRate
name|getRate
parameter_list|()
block|{
return|return
name|rate
return|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|rate
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

