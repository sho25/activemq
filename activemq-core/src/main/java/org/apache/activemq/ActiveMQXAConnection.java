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
package|;
end_package

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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAQueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAQueueSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XASession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XATopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XATopicSession
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
name|management
operator|.
name|JMSStatsImpl
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
name|Transport
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
name|IdGenerator
import|;
end_import

begin_comment
comment|/**  * The XAConnection interface extends the capability of Connection by providing  * an XASession (optional).  *<p/>  * The XAConnection interface is optional. JMS providers are not required to  * support this interface. This interface is for use by JMS providers to  * support transactional environments. Client programs are strongly encouraged  * to use the transactional support  available in their environment, rather  * than use these XA  interfaces directly.  *  *   * @see javax.jms.Connection  * @see javax.jms.ConnectionFactory  * @see javax.jms.QueueConnection  * @see javax.jms.TopicConnection  * @see javax.jms.TopicConnectionFactory  * @see javax.jms.QueueConnection  * @see javax.jms.QueueConnectionFactory  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQXAConnection
extends|extends
name|ActiveMQConnection
implements|implements
name|XATopicConnection
implements|,
name|XAQueueConnection
implements|,
name|XAConnection
block|{
specifier|protected
name|ActiveMQXAConnection
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|IdGenerator
name|clientIdGenerator
parameter_list|,
name|IdGenerator
name|connectionIdGenerator
parameter_list|,
name|JMSStatsImpl
name|factoryStats
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|transport
argument_list|,
name|clientIdGenerator
argument_list|,
name|connectionIdGenerator
argument_list|,
name|factoryStats
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XASession
name|createXASession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XASession
operator|)
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
return|;
block|}
specifier|public
name|XATopicSession
name|createXATopicSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XATopicSession
operator|)
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
return|;
block|}
specifier|public
name|XAQueueSession
name|createXAQueueSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XAQueueSession
operator|)
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
return|;
block|}
specifier|public
name|Session
name|createSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|acknowledgeMode
parameter_list|)
throws|throws
name|JMSException
block|{
name|checkClosedOrFailed
argument_list|()
expr_stmt|;
name|ensureConnectionInfoSent
argument_list|()
expr_stmt|;
return|return
operator|new
name|ActiveMQXASession
argument_list|(
name|this
argument_list|,
name|getNextSessionId
argument_list|()
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|,
name|isDispatchAsync
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

