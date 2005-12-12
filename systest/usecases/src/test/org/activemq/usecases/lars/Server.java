begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
operator|.
name|lars
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|Server
implements|implements
name|MessageListener
block|{
specifier|private
name|String
name|configFile
init|=
literal|"src/conf/activemq.xml"
decl_stmt|;
specifier|private
name|QueueConnectionFactory
name|cf
decl_stmt|;
specifier|private
name|QueueConnection
name|queueConnection
decl_stmt|;
specifier|private
name|QueueSession
name|queueSession
decl_stmt|;
specifier|private
name|Queue
name|queue
decl_stmt|;
specifier|private
name|MessageConsumer
name|messageConsumer
decl_stmt|;
specifier|private
name|QueueSender
name|queueSender
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|messagePrefix
init|=
literal|"X"
decl_stmt|;
specifier|private
name|int
name|messageMultiplier
init|=
literal|1
decl_stmt|;
specifier|public
name|Server
parameter_list|()
throws|throws
name|JMSException
block|{
name|super
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|connectionFactory
operator|.
name|setUseEmbeddedBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setBrokerURL
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setBrokerXmlConfig
argument_list|(
literal|"file:"
operator|+
name|configFile
argument_list|)
expr_stmt|;
name|cf
operator|=
name|connectionFactory
expr_stmt|;
name|initQueue
argument_list|()
expr_stmt|;
name|initListener
argument_list|()
expr_stmt|;
name|initSender
argument_list|()
expr_stmt|;
name|queueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param msgPfx May only contain one capital letter.      * @throws JMSException      */
specifier|public
name|Server
parameter_list|(
name|String
name|msgPfx
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
argument_list|()
expr_stmt|;
name|messagePrefix
operator|=
name|msgPfx
expr_stmt|;
name|char
name|letter
init|=
name|messagePrefix
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|pow
init|=
name|letter
operator|-
literal|'A'
decl_stmt|;
if|if
condition|(
name|pow
operator|>
literal|0
operator|&&
name|pow
operator|<=
literal|'Z'
operator|-
literal|'A'
condition|)
block|{
name|messageMultiplier
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|10
argument_list|,
name|pow
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initQueue
parameter_list|()
throws|throws
name|JMSException
block|{
name|queueConnection
operator|=
name|cf
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|queueSession
operator|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|javax
operator|.
name|jms
operator|.
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queue
operator|=
name|queueSession
operator|.
name|createQueue
argument_list|(
literal|"test_queue"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initSender
parameter_list|()
throws|throws
name|JMSException
block|{
name|queueSender
operator|=
name|queueSession
operator|.
name|createSender
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|queueSender
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|queueSender
operator|.
name|setTimeToLive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initListener
parameter_list|()
throws|throws
name|JMSException
block|{
name|messageConsumer
operator|=
name|queueSession
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|messageConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
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
if|if
condition|(
name|message
operator|instanceof
name|MapMessage
condition|)
block|{
try|try
block|{
name|MapMessage
name|msg
init|=
operator|(
name|MapMessage
operator|)
name|message
decl_stmt|;
name|String
name|command
init|=
name|msg
operator|.
name|getStringProperty
argument_list|(
literal|"cmd"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|messagePrefix
operator|+
name|command
argument_list|)
expr_stmt|;
name|msg
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown message type"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|void
name|sendMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
name|MapMessage
name|message
init|=
name|getMapMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"cmd"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
name|messageMultiplier
argument_list|)
argument_list|)
expr_stmt|;
name|queueSender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapMessage
name|getMapMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|queueSession
operator|.
name|createMapMessage
argument_list|()
return|;
block|}
specifier|public
name|void
name|go
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
name|i
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
block|}
block|}
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
try|try
block|{
name|Server
name|s
init|=
operator|new
name|Server
argument_list|()
decl_stmt|;
name|s
operator|.
name|go
argument_list|()
expr_stmt|;
comment|//System.exit(0);
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
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

