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
name|transport
operator|.
name|amqp
package|;
end_package

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
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|Wait
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
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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

begin_comment
comment|/**  * Test that connection attempts that don't send the connect performative  * get cleaned up by the inactivity monitor.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AmqpConnectTimeoutTest
extends|extends
name|AmqpTestSupport
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
name|AmqpConnectTimeoutTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Socket
name|connection
decl_stmt|;
specifier|protected
name|boolean
name|useSSL
decl_stmt|;
specifier|protected
name|String
name|connectorScheme
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"amqp"
block|,
literal|false
block|}
block|,
block|{
literal|"amqp+ssl"
block|,
literal|true
block|}
block|,
block|{
literal|"amqp+nio"
block|,
literal|false
block|}
block|,
block|{
literal|"amqp+nio+ssl"
block|,
literal|true
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|AmqpConnectTimeoutTest
parameter_list|(
name|String
name|connectorScheme
parameter_list|,
name|boolean
name|useSSL
parameter_list|)
block|{
name|this
operator|.
name|connectorScheme
operator|=
name|connectorScheme
expr_stmt|;
name|this
operator|.
name|useSSL
operator|=
name|useSSL
expr_stmt|;
block|}
specifier|protected
name|String
name|getConnectorScheme
parameter_list|()
block|{
return|return
name|connectorScheme
return|;
block|}
specifier|protected
name|boolean
name|isUseSSL
parameter_list|()
block|{
return|return
name|useSSL
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseSslConnector
parameter_list|()
block|{
return|return
name|isUseSSL
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseNioConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseNioPlusSslConnector
parameter_list|()
block|{
return|return
name|isUseSSL
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
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
name|Throwable
name|e
parameter_list|)
block|{}
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|"&transport.connectAttemptTimeout=1200"
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testInactivityMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
literal|'A'
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getOutputStream
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"unexpected exception on connect/disconnect"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"one connection"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|1
operator|==
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
name|getConnectorScheme
argument_list|()
argument_list|)
operator|.
name|connectionCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|250
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// and it should be closed due to inactivity
name|assertTrue
argument_list|(
literal|"no dangling connections"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|0
operator|==
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
name|getConnectorScheme
argument_list|()
argument_list|)
operator|.
name|connectionCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|500
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions"
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Socket
name|createConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|port
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|connectorScheme
condition|)
block|{
case|case
literal|"amqp"
case|:
name|port
operator|=
name|this
operator|.
name|amqpPort
expr_stmt|;
break|break;
case|case
literal|"amqp+ssl"
case|:
name|port
operator|=
name|this
operator|.
name|amqpSslPort
expr_stmt|;
break|break;
case|case
literal|"amqp+nio"
case|:
name|port
operator|=
name|this
operator|.
name|amqpNioPort
expr_stmt|;
break|break;
case|case
literal|"amqp+nio+ssl"
case|:
name|port
operator|=
name|this
operator|.
name|amqpNioPlusSslPort
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid AMQP connector scheme passed to test."
argument_list|)
throw|;
block|}
if|if
condition|(
name|isUseSSL
argument_list|()
condition|)
block|{
return|return
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createSocket
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Socket
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

