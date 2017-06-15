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
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|JMSException
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

begin_class
specifier|public
class|class
name|JMSClientTestSupport
extends|extends
name|AmqpTestSupport
block|{
specifier|protected
name|Connection
name|connection
decl_stmt|;
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
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"tearDown started."
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"in CloseConnectionTask.call(), calling connection.close()"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return the proper destination name to use for each test method invocation.      */
specifier|protected
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|name
operator|.
name|getMethodName
argument_list|()
return|;
block|}
comment|/**      * Can be overridden in subclasses to test against a different transport suchs as NIO.      *      * @return the URI to connect to on the Broker for AMQP.      */
specifier|protected
name|URI
name|getBrokerURI
parameter_list|()
block|{
return|return
name|amqpURI
return|;
block|}
specifier|protected
name|URI
name|getAmqpURI
parameter_list|()
block|{
return|return
name|getAmqpURI
argument_list|(
literal|""
argument_list|)
return|;
block|}
specifier|protected
name|URI
name|getAmqpURI
parameter_list|(
name|String
name|uriOptions
parameter_list|)
block|{
name|String
name|clientScheme
decl_stmt|;
name|boolean
name|useSSL
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|getBrokerURI
argument_list|()
operator|.
name|getScheme
argument_list|()
condition|)
block|{
case|case
literal|"tcp"
case|:
case|case
literal|"amqp"
case|:
case|case
literal|"auto"
case|:
case|case
literal|"amqp+nio"
case|:
case|case
literal|"auto+nio"
case|:
name|clientScheme
operator|=
literal|"amqp://"
expr_stmt|;
break|break;
case|case
literal|"ssl"
case|:
case|case
literal|"amqp+ssl"
case|:
case|case
literal|"auto+ssl"
case|:
case|case
literal|"amqp+nio+ssl"
case|:
case|case
literal|"auto+nio+ssl"
case|:
name|clientScheme
operator|=
literal|"amqps://"
expr_stmt|;
name|useSSL
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|"ws"
case|:
case|case
literal|"amqp+ws"
case|:
name|clientScheme
operator|=
literal|"amqpws://"
expr_stmt|;
break|break;
case|case
literal|"wss"
case|:
case|case
literal|"amqp+wss"
case|:
name|clientScheme
operator|=
literal|"amqpwss://"
expr_stmt|;
name|useSSL
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
name|clientScheme
operator|=
literal|"amqp://"
expr_stmt|;
block|}
name|String
name|amqpURI
init|=
name|clientScheme
operator|+
name|getBrokerURI
argument_list|()
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|getBrokerURI
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|uriOptions
operator|!=
literal|null
operator|&&
operator|!
name|uriOptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|uriOptions
operator|.
name|startsWith
argument_list|(
literal|"?"
argument_list|)
operator|||
name|uriOptions
operator|.
name|startsWith
argument_list|(
literal|"&"
argument_list|)
condition|)
block|{
name|uriOptions
operator|=
name|uriOptions
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|uriOptions
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|useSSL
condition|)
block|{
name|amqpURI
operator|+=
literal|"?transport.verifyHost=false"
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|uriOptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|useSSL
condition|)
block|{
name|amqpURI
operator|+=
literal|"&"
operator|+
name|uriOptions
expr_stmt|;
block|}
else|else
block|{
name|amqpURI
operator|+=
literal|"?"
operator|+
name|uriOptions
expr_stmt|;
block|}
block|}
name|URI
name|result
init|=
name|getBrokerURI
argument_list|()
decl_stmt|;
try|try
block|{
name|result
operator|=
operator|new
name|URI
argument_list|(
name|amqpURI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{         }
return|return
name|result
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|,
name|syncPublish
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|String
name|clientId
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|clientId
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|getBrokerURI
argument_list|()
argument_list|,
literal|"admin"
argument_list|,
literal|"password"
argument_list|,
name|clientId
argument_list|,
name|syncPublish
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
end_class

end_unit

