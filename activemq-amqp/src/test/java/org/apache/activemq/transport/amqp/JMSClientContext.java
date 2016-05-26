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
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
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
name|jms
operator|.
name|JmsConnectionFactory
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
comment|/**  * Context used for AMQP JMS Clients to create connection instances.  */
end_comment

begin_class
specifier|public
class|class
name|JMSClientContext
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
name|JMSClientContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|JMSClientContext
name|INSTANCE
init|=
operator|new
name|JMSClientContext
argument_list|()
decl_stmt|;
comment|//----- Plain JMS Connection Create methods ------------------------------//
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|remoteURI
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
name|syncPublish
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|clientId
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|clientId
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|clientId
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|syncPublish
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception "
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
return|return
name|connection
return|;
block|}
comment|//----- JMS TopicConnection Create methods -------------------------------//
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createTopicConnection
argument_list|(
name|remoteURI
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createTopicConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createTopicConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
name|syncPublish
argument_list|)
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|clientId
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createTopicConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|clientId
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|clientId
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
name|TopicConnectionFactory
name|factory
init|=
name|createTopicConnectionFactory
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|syncPublish
argument_list|)
decl_stmt|;
name|TopicConnection
name|connection
init|=
name|factory
operator|.
name|createTopicConnection
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception "
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
return|return
name|connection
return|;
block|}
comment|//----- JMS QueueConnection Create methods -------------------------------//
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createQueueConnection
argument_list|(
name|remoteURI
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createQueueConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createQueueConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
name|syncPublish
argument_list|)
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|clientId
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createQueueConnection
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|clientId
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|clientId
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
throws|throws
name|JMSException
block|{
name|QueueConnectionFactory
name|factory
init|=
name|createQueueConnectionFactory
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|syncPublish
argument_list|)
decl_stmt|;
name|QueueConnection
name|connection
init|=
name|factory
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception "
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
return|return
name|connection
return|;
block|}
comment|//------ Internal Implementation bits ------------------------------------//
specifier|private
name|QueueConnectionFactory
name|createQueueConnectionFactory
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
block|{
return|return
operator|(
name|QueueConnectionFactory
operator|)
name|createConnectionFactory
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|syncPublish
argument_list|)
return|;
block|}
specifier|private
name|TopicConnectionFactory
name|createTopicConnectionFactory
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
block|{
return|return
operator|(
name|TopicConnectionFactory
operator|)
name|createConnectionFactory
argument_list|(
name|remoteURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|syncPublish
argument_list|)
return|;
block|}
specifier|private
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|syncPublish
parameter_list|)
block|{
name|boolean
name|useSSL
init|=
name|remoteURI
operator|.
name|getScheme
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ssl"
argument_list|)
decl_stmt|;
name|String
name|amqpURI
init|=
operator|(
name|useSSL
condition|?
literal|"amqps://"
else|:
literal|"amqp://"
operator|)
operator|+
name|remoteURI
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|remoteURI
operator|.
name|getPort
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"In createConnectionFactory using URI: {}"
argument_list|,
name|amqpURI
argument_list|)
expr_stmt|;
name|JmsConnectionFactory
name|factory
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|amqpURI
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setUsername
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setForceSyncSend
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
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

