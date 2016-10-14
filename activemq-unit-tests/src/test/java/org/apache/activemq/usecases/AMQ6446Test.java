begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|usecases
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
name|BrokerFactory
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
name|activemq
operator|.
name|util
operator|.
name|DefaultTestAppender
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
name|Level
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ6446Test
block|{
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
name|LinkedList
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test2Connections
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|urlTraceParam
init|=
literal|"?trace=true"
decl_stmt|;
name|startBroker
argument_list|(
name|urlTraceParam
argument_list|)
expr_stmt|;
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|loggers
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|messages
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|DefaultTestAppender
name|appender
init|=
operator|new
name|DefaultTestAppender
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doAppend
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
name|loggers
operator|.
name|add
argument_list|(
name|event
operator|.
name|getLoggerName
argument_list|()
argument_list|)
expr_stmt|;
name|messages
operator|.
name|add
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|String
name|brokerUrlWithTrace
init|=
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
name|urlTraceParam
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrlWithTrace
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|c
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
comment|// no logger ends with :2
name|assertFalse
argument_list|(
name|foundMatch
argument_list|(
name|loggers
argument_list|,
literal|".*:2$"
argument_list|)
argument_list|)
expr_stmt|;
comment|// starts with 000000x:
name|assertTrue
argument_list|(
name|foundMatch
argument_list|(
name|messages
argument_list|,
literal|"^0+\\d:.*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|foundMatch
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|String
name|regex
parameter_list|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|input
range|:
name|values
control|)
block|{
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|found
operator|=
name|m
operator|.
name|matches
argument_list|()
expr_stmt|;
if|if
condition|(
name|found
condition|)
block|{
break|break;
block|}
block|}
return|return
name|found
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test2ConnectionsLegacy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|legacySupportParam
init|=
literal|"?trace=true&jmxPort=22"
decl_stmt|;
name|startBroker
argument_list|(
name|legacySupportParam
argument_list|)
expr_stmt|;
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|loggers
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|messages
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|DefaultTestAppender
name|appender
init|=
operator|new
name|DefaultTestAppender
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doAppend
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
name|loggers
operator|.
name|add
argument_list|(
name|event
operator|.
name|getLoggerName
argument_list|()
argument_list|)
expr_stmt|;
name|messages
operator|.
name|add
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|String
name|brokerUrlWithTrace
init|=
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
name|legacySupportParam
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrlWithTrace
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|c
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
comment|// logger ends with :2
name|assertTrue
argument_list|(
name|foundMatch
argument_list|(
name|loggers
argument_list|,
literal|".*:2$"
argument_list|)
argument_list|)
expr_stmt|;
comment|// starts with 000000x:
name|assertFalse
argument_list|(
name|foundMatch
argument_list|(
name|messages
argument_list|,
literal|"^0+\\d:.*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Connection
name|connection
range|:
name|connections
control|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{}
block|}
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|(
name|String
name|urlParam
parameter_list|)
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(tcp://0.0.0.0:0"
operator|+
name|urlParam
operator|+
literal|")/localhost?useJmx=false&persistent=false"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
