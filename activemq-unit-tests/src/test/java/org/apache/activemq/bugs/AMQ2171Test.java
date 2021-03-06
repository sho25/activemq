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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
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
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ2171Test
implements|implements
name|Thread
operator|.
name|UncaughtExceptionHandler
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
name|AMQ2171Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|QUEUE_SIZE
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|static
name|Queue
name|destination
decl_stmt|;
specifier|private
name|String
name|brokerUri
decl_stmt|;
specifier|private
name|String
name|brokerUriNoPrefetch
decl_stmt|;
specifier|private
name|Collection
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start an embedded broker up.
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|BROKER_URL
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerUri
operator|=
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|brokerUriNoPrefetch
operator|=
name|brokerUri
operator|+
literal|"?jms.prefetchPolicy.all=0"
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
expr_stmt|;
name|produce
argument_list|(
name|brokerUri
argument_list|,
name|QUEUE_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|addHandler
parameter_list|()
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|this
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
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testBrowsePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|brokerUri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testBrowseNoPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|brokerUriNoPrefetch
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|runTest
parameter_list|(
name|String
name|brokerURL
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|start
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Enumeration
argument_list|<
name|Message
argument_list|>
name|unread
init|=
operator|(
name|Enumeration
argument_list|<
name|Message
argument_list|>
operator|)
name|session
operator|.
name|createBrowser
argument_list|(
name|destination
argument_list|)
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|unread
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|unread
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|QUEUE_SIZE
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|JMSException
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|produce
parameter_list|(
name|String
name|brokerURL
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|i
operator|+
literal|1
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Message "
operator|+
name|id
argument_list|)
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"MsgNumber"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sent "
operator|+
name|id
operator|+
literal|", ith "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
try|try
block|{
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
block|}
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

