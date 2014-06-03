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
name|ExceptionListener
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
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|ConnectionFactoryImpl
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
name|Exception
name|e
parameter_list|)
block|{             }
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
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
comment|/**      * Can be overridden in subclasses to test against a different transport suchs as NIO.      *      * @return the port to connect to on the Broker.      */
specifier|protected
name|int
name|getBrokerPort
parameter_list|()
block|{
return|return
name|port
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
parameter_list|,
name|boolean
name|useSsl
parameter_list|)
throws|throws
name|JMSException
block|{
name|int
name|brokerPort
init|=
name|getBrokerPort
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating connection on port {}"
argument_list|,
name|brokerPort
argument_list|)
expr_stmt|;
specifier|final
name|ConnectionFactoryImpl
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|brokerPort
argument_list|,
literal|"admin"
argument_list|,
literal|"password"
argument_list|,
literal|null
argument_list|,
name|useSsl
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setSyncPublish
argument_list|(
name|syncPublish
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTopicPrefix
argument_list|(
literal|"topic://"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setQueuePrefix
argument_list|(
literal|"queue://"
argument_list|)
expr_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
operator|&&
operator|!
name|clientId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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

