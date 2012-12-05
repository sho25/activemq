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
name|tool
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
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|MemProducer
block|{
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|public
name|MemProducer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|connection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|Session
name|s
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
name|producer
operator|=
name|s
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDeliveryMode
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|JMSException
block|{
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
throws|throws
name|JMSException
block|{
name|sendMessage
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/*     *   allow producer to attach message counter on its header. This will be used to verify message order     *     */
specifier|public
name|void
name|sendMessage
parameter_list|(
name|Message
name|msg
parameter_list|,
name|String
name|headerName
parameter_list|,
name|long
name|headerValue
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|headerName
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setLongProperty
argument_list|(
name|headerName
argument_list|,
name|headerValue
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
