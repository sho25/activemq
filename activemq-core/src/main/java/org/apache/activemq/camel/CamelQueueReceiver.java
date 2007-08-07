begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQSession
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
name|Endpoint
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueReceiver
import|;
end_import

begin_comment
comment|/**  * A JMS {@link javax.jms.QueueReceiver} which consumes message exchanges from a  * Camel {@link org.apache.camel.Endpoint}  *  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|CamelQueueReceiver
extends|extends
name|CamelMessageConsumer
implements|implements
name|QueueReceiver
block|{
specifier|public
name|CamelQueueReceiver
parameter_list|(
name|CamelQueue
name|destination
parameter_list|,
name|Endpoint
name|endpoint
parameter_list|,
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|destination
argument_list|,
name|endpoint
argument_list|,
name|session
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the<CODE>Queue</CODE> associated with this queue receiver.      *      * @return this receiver's<CODE>Queue</CODE>      * @throws JMSException if the JMS provider fails to get the queue for this queue      *                      receiver due to some internal error.      */
specifier|public
name|Queue
name|getQueue
parameter_list|()
throws|throws
name|JMSException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
operator|(
name|Queue
operator|)
name|super
operator|.
name|getDestination
argument_list|()
return|;
block|}
block|}
end_class

end_unit

