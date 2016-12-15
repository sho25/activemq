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
name|karaf
operator|.
name|itest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|Callable
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|PaxExam
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|options
operator|.
name|WrappedUrlProvisionOption
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
name|Message
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
name|TemporaryQueue
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
name|assertEquals
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
name|assertNotNull
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
annotation|@
name|RunWith
argument_list|(
name|PaxExam
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ActiveMQBrokerFeatureTest
extends|extends
name|AbstractJmsFeatureTest
block|{
annotation|@
name|Configuration
specifier|public
specifier|static
name|Option
index|[]
name|configure
parameter_list|()
block|{
return|return
name|configureBrokerStart
argument_list|(
name|configure
argument_list|(
literal|"activemq"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"../../../classes/META-INF/maven/dependencies.properties"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|String
name|getArtifactVersion
parameter_list|(
specifier|final
name|String
name|groupId
parameter_list|,
specifier|final
name|String
name|artifactId
parameter_list|)
block|{
specifier|final
name|Properties
name|dependencies
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
init|(
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|dependencies
operator|.
name|load
argument_list|(
name|fis
argument_list|)
expr_stmt|;
specifier|final
name|String
name|version
init|=
name|dependencies
operator|.
name|getProperty
argument_list|(
name|groupId
operator|+
literal|"/"
operator|+
name|artifactId
operator|+
literal|"/version"
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not resolve version. Do you have a dependency for "
operator|+
name|groupId
operator|+
literal|"/"
operator|+
name|artifactId
operator|+
literal|" in your maven project?"
argument_list|)
throw|;
block|}
return|return
name|version
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not resolve version for groupId:"
operator|+
name|groupId
operator|+
literal|" artifactId:"
operator|+
name|artifactId
operator|+
literal|" by reading the dependency information generated by maven."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
name|installWrappedBundle
parameter_list|(
name|WrappedUrlProvisionOption
name|option
parameter_list|)
block|{
return|return
name|executeCommand
argument_list|(
literal|"bundle:install 'wrap:"
operator|+
name|option
operator|.
name|getURL
argument_list|()
operator|+
literal|"'"
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Throwable
block|{
name|withinReason
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"brokerName = amq-broker"
argument_list|,
name|executeCommand
argument_list|(
literal|"activemq:list"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|withinReason
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|executeCommand
argument_list|(
literal|"activemq:bstat"
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|contains
argument_list|(
literal|"BrokerName = amq-broker"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// produce and consume
specifier|final
name|String
name|nameAndPayload
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|produceMessage
argument_list|(
name|nameAndPayload
argument_list|)
expr_stmt|;
name|executeCommand
argument_list|(
literal|"activemq:bstat"
argument_list|,
name|COMMAND_TIMEOUT
argument_list|,
literal|false
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|withinReason
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"JMS_BODY_FIELD:JMSText = "
operator|+
name|nameAndPayload
argument_list|,
name|executeCommand
argument_list|(
literal|"activemq:browse --amqurl tcp://localhost:61616 --user karaf --password karaf -Vbody "
operator|+
name|nameAndPayload
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got our message"
argument_list|,
name|nameAndPayload
argument_list|,
name|consumeMessage
argument_list|(
name|nameAndPayload
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testTemporaryDestinations
parameter_list|()
throws|throws
name|Throwable
block|{
name|Connection
name|connection
init|=
name|getConnection
argument_list|()
decl_stmt|;
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
name|TemporaryQueue
name|temporaryQueue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|session
operator|.
name|createProducer
argument_list|(
name|temporaryQueue
argument_list|)
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|temporaryQueue
argument_list|)
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Didn't receive the message"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

