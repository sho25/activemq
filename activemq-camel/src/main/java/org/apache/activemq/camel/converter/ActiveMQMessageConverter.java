begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|camel
operator|.
name|converter
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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
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
name|ActiveMQObjectMessage
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
name|ActiveMQTextMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Converter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Exchange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Processor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsBinding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsEndpoint
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
annotation|@
name|Converter
specifier|public
class|class
name|ActiveMQMessageConverter
block|{
specifier|private
name|JmsBinding
name|binding
init|=
operator|new
name|JmsBinding
argument_list|()
decl_stmt|;
comment|/**      * Converts the inbound message exchange to an ActiveMQ JMS message      *      * @return the ActiveMQ message      */
annotation|@
name|Converter
specifier|public
name|ActiveMQMessage
name|toMessage
parameter_list|(
name|Exchange
name|exchange
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQMessage
name|message
init|=
name|createActiveMQMessage
argument_list|(
name|exchange
argument_list|)
decl_stmt|;
name|getBinding
argument_list|()
operator|.
name|appendJmsProperties
argument_list|(
name|message
argument_list|,
name|exchange
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
comment|/**      * Allows a JMS {@link MessageListener} to be converted to a Camel {@link Processor}      * so that we can provide better      *<a href="">Bean Integration</a> so that we can use any JMS MessageListener in      * in Camel as a bean      * @param listener the JMS message listener      * @return a newly created Camel Processor which when invoked will invoke      * {@link MessageListener#onMessage(Message)}      */
annotation|@
name|Converter
specifier|public
name|Processor
name|toProcessor
parameter_list|(
specifier|final
name|MessageListener
name|listener
parameter_list|)
block|{
return|return
operator|new
name|Processor
argument_list|()
block|{
specifier|public
name|void
name|process
parameter_list|(
name|Exchange
name|exchange
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|toMessage
argument_list|(
name|exchange
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Processor of MessageListener: "
operator|+
name|listener
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|ActiveMQMessage
name|createActiveMQMessage
parameter_list|(
name|Exchange
name|exchange
parameter_list|)
throws|throws
name|JMSException
block|{
name|Object
name|body
init|=
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|getBody
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|instanceof
name|String
condition|)
block|{
name|ActiveMQTextMessage
name|answer
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setText
argument_list|(
operator|(
name|String
operator|)
name|body
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
elseif|else
if|if
condition|(
name|body
operator|instanceof
name|Serializable
condition|)
block|{
name|ActiveMQObjectMessage
name|answer
init|=
operator|new
name|ActiveMQObjectMessage
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setObject
argument_list|(
operator|(
name|Serializable
operator|)
name|body
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQMessage
argument_list|()
return|;
block|}
block|}
comment|// Properties
comment|//-------------------------------------------------------------------------
specifier|public
name|JmsBinding
name|getBinding
parameter_list|()
block|{
return|return
name|binding
return|;
block|}
specifier|public
name|void
name|setBinding
parameter_list|(
name|JmsBinding
name|binding
parameter_list|)
block|{
name|this
operator|.
name|binding
operator|=
name|binding
expr_stmt|;
block|}
block|}
end_class

end_unit

