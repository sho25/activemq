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
name|network
operator|.
name|jms
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
name|Destination
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
name|Message
import|;
end_import

begin_comment
comment|/**  * Converts Message from one JMS to another  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|SimpleJmsMessageConvertor
implements|implements
name|JmsMesageConvertor
block|{
comment|/**      * Convert a foreign JMS Message to a native ActiveMQ Message - Inbound or      * visa-versa outbound.      *      * @param message      *      The target message to convert to a native ActiveMQ message      * @return the converted message      * @throws JMSException      */
specifier|public
name|Message
name|convert
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|message
return|;
block|}
comment|/**      * Convert a foreign JMS Message to a native ActiveMQ Message - Inbound or      * visa-versa outbound.  If the replyTo Destination instance is not null      * then the Message is configured with the given replyTo value.      *      * @param message      *      The target message to convert to a native ActiveMQ message      * @param replyTo      *      The replyTo Destination to set on the converted Message.      *      * @return the converted message      * @throws JMSException      */
specifier|public
name|Message
name|convert
parameter_list|(
name|Message
name|message
parameter_list|,
name|Destination
name|replyTo
parameter_list|)
throws|throws
name|JMSException
block|{
name|Message
name|msg
init|=
name|convert
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|replyTo
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSReplyTo
argument_list|(
name|replyTo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|setJMSReplyTo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
block|}
specifier|public
name|void
name|setConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
end_class

end_unit

