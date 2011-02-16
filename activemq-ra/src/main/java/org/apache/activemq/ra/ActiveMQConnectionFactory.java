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
name|ra
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|javax
operator|.
name|naming
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|Referenceable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionManager
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
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionFactory
implements|implements
name|ConnectionFactory
implements|,
name|QueueConnectionFactory
implements|,
name|TopicConnectionFactory
implements|,
name|Referenceable
implements|,
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5754338187296859149L
decl_stmt|;
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
name|ActiveMQConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ConnectionManager
name|manager
decl_stmt|;
specifier|private
name|ActiveMQManagedConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|Reference
name|reference
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQConnectionRequestInfo
name|info
decl_stmt|;
comment|/**      * @param factory      * @param manager      * @param connectionRequestInfo      */
specifier|public
name|ActiveMQConnectionFactory
parameter_list|(
name|ActiveMQManagedConnectionFactory
name|factory
parameter_list|,
name|ConnectionManager
name|manager
parameter_list|,
name|ActiveMQConnectionRequestInfo
name|connectionRequestInfo
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|connectionRequestInfo
expr_stmt|;
block|}
comment|/**      * @see javax.jms.ConnectionFactory#createConnection()      */
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
name|info
operator|.
name|copy
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @see javax.jms.ConnectionFactory#createConnection(java.lang.String,      *      java.lang.String)      */
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionRequestInfo
name|i
init|=
name|info
operator|.
name|copy
argument_list|()
decl_stmt|;
name|i
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|i
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
return|return
name|createConnection
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/**      * @param connectionRequestInfo      * @return      * @throws JMSException      */
specifier|private
name|Connection
name|createConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|connectionRequestInfo
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
if|if
condition|(
name|connectionRequestInfo
operator|.
name|isUseInboundSessionEnabled
argument_list|()
condition|)
block|{
return|return
operator|new
name|InboundConnectionProxy
argument_list|()
return|;
block|}
if|if
condition|(
name|manager
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"No JCA ConnectionManager configured! Either enable UseInboundSessionEnabled or get your JCA container to configure one."
argument_list|)
throw|;
block|}
return|return
operator|(
name|Connection
operator|)
name|manager
operator|.
name|allocateConnection
argument_list|(
name|factory
argument_list|,
name|connectionRequestInfo
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ResourceException
name|e
parameter_list|)
block|{
comment|// Throw the root cause if it was a JMSException..
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|JMSException
condition|)
block|{
throw|throw
operator|(
name|JMSException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connection could not be created:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|JMSException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.naming.Referenceable#getReference()      */
specifier|public
name|Reference
name|getReference
parameter_list|()
block|{
return|return
name|reference
return|;
block|}
comment|/**      * @see javax.resource.Referenceable#setReference(javax.naming.Reference)      */
specifier|public
name|void
name|setReference
parameter_list|(
name|Reference
name|reference
parameter_list|)
block|{
name|this
operator|.
name|reference
operator|=
name|reference
expr_stmt|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|QueueConnection
operator|)
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|QueueConnection
operator|)
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|TopicConnection
operator|)
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|TopicConnection
operator|)
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
block|}
end_class

end_unit

