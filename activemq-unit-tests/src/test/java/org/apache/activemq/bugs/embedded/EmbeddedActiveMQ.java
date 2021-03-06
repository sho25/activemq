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
name|bugs
operator|.
name|embedded
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Message
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
specifier|public
class|class
name|EmbeddedActiveMQ
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|EmbeddedActiveMQ
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Start..."
argument_list|)
expr_stmt|;
try|try
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"TestMQ"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Broker '"
operator|+
name|brokerService
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|"' is starting........"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://TestMQ"
argument_list|)
decl_stmt|;
name|connection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|Session
name|session
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
name|Destination
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.QUEUE"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
name|ThreadExplorer
operator|.
name|show
argument_list|(
literal|"Active threads after start:"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Press return to stop........"
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Broker '"
operator|+
name|brokerService
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|"' is stopping........"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|sleep
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|ThreadExplorer
operator|.
name|show
argument_list|(
literal|"Active threads after stop:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for list theads is greater then 1 ..."
argument_list|)
expr_stmt|;
name|int
name|numTh
init|=
name|ThreadExplorer
operator|.
name|active
argument_list|()
decl_stmt|;
while|while
condition|(
name|numTh
operator|>
literal|2
condition|)
block|{
name|sleep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|numTh
operator|=
name|ThreadExplorer
operator|.
name|active
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|ThreadExplorer
operator|.
name|show
argument_list|(
literal|"Still active threads:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stop..."
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|sleep
parameter_list|(
name|int
name|second
parameter_list|)
block|{
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for "
operator|+
name|second
operator|+
literal|"s..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|second
operator|*
literal|1000L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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

