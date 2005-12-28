begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|activemq
operator|.
name|benchmark
package|;
end_package

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
name|TextMessage
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
comment|/**  * @author James Strachan  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|Consumer
extends|extends
name|BenchmarkSupport
implements|implements
name|MessageListener
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Consumer
name|tool
init|=
operator|new
name|Consumer
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|tool
operator|.
name|setUrl
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|tool
operator|.
name|setTopic
argument_list|(
name|parseBoolean
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|tool
operator|.
name|setSubject
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|tool
operator|.
name|setDurable
argument_list|(
name|parseBoolean
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|tool
operator|.
name|setConnectionCount
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|tool
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Consumer
parameter_list|()
block|{     }
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|JMSException
block|{
name|start
argument_list|()
expr_stmt|;
name|subscribe
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|subscribe
parameter_list|()
throws|throws
name|JMSException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subjects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subscribe
argument_list|(
name|subjects
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|subscribe
parameter_list|(
name|String
name|subject
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session
init|=
name|createSession
argument_list|()
decl_stmt|;
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|subject
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consuming on : "
operator|+
name|destination
operator|+
literal|" of type: "
operator|+
name|destination
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isDurable
argument_list|()
operator|&&
name|isTopic
argument_list|()
condition|)
block|{
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
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
name|addResource
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
comment|// lets force the content to be deserialized
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|count
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// lets count the messages
comment|//message.acknowledge();
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

