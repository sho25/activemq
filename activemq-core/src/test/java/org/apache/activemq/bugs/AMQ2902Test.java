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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|JMSException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|TransportConnection
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
name|transport
operator|.
name|TransportDisposedIOException
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
name|Appender
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
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ2902Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ2580Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotExceptionInLog
init|=
operator|new
name|AtomicBoolean
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failedToFindMDC
init|=
operator|new
name|AtomicBoolean
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|Appender
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
if|if
condition|(
name|event
operator|.
name|getThrowableInformation
argument_list|()
operator|!=
literal|null
operator|&&
name|event
operator|.
name|getThrowableInformation
argument_list|()
operator|.
name|getThrowable
argument_list|()
operator|instanceof
name|TransportDisposedIOException
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"got event: "
operator|+
name|event
operator|+
literal|", ex:"
operator|+
name|event
operator|.
name|getThrowableInformation
argument_list|()
operator|.
name|getThrowable
argument_list|()
argument_list|,
name|event
operator|.
name|getThrowableInformation
argument_list|()
operator|.
name|getThrowable
argument_list|()
argument_list|)
expr_stmt|;
name|gotExceptionInLog
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|.
name|getMDC
argument_list|(
literal|"activemq.broker"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|failedToFindMDC
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
decl_stmt|;
specifier|public
name|void
name|testNoExceptionOnClosewithStartStop
parameter_list|()
throws|throws
name|JMSException
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoExceptionOnClose
parameter_list|()
throws|throws
name|JMSException
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|gotExceptionInLog
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|failedToFindMDC
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
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
name|getLogger
argument_list|(
name|TransportConnection
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".Transport"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getLogger
argument_list|(
name|TransportConnection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|assertFalse
argument_list|(
literal|"got unexpected ex in log on graceful close"
argument_list|,
name|gotExceptionInLog
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"MDC is there"
argument_list|,
name|failedToFindMDC
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

