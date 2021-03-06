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
name|usecases
package|;
end_package

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
name|ActiveMQTopic
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
name|test
operator|.
name|JmsTopicSendReceiveWithTwoConnectionsTest
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
name|udp
operator|.
name|UdpTransportUsingServerTest
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
name|CompositeConsumeTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsTest
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
name|CompositeConsumeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|messages
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|getDestinations
argument_list|()
decl_stmt|;
name|int
name|destIdx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"About to send a message: "
operator|+
name|message
operator|+
literal|" with text: "
operator|+
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|destinations
index|[
name|destIdx
index|]
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|destIdx
operator|>=
name|destinations
operator|.
name|length
condition|)
block|{
name|destIdx
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|assertMessagesAreReceived
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the subscription subject      */
specifier|protected
name|String
name|getSubject
parameter_list|()
block|{
return|return
name|getPrefix
argument_list|()
operator|+
literal|"FOO.BAR,"
operator|+
name|getPrefix
argument_list|()
operator|+
literal|"FOO.X.Y,"
operator|+
name|getPrefix
argument_list|()
operator|+
literal|"BAR.>"
return|;
block|}
comment|/**      * Returns the destinations on which we publish      */
specifier|protected
name|Destination
index|[]
name|getDestinations
parameter_list|()
block|{
return|return
operator|new
name|Destination
index|[]
block|{
operator|new
name|ActiveMQTopic
argument_list|(
name|getPrefix
argument_list|()
operator|+
literal|"FOO.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
name|getPrefix
argument_list|()
operator|+
literal|"BAR.WHATNOT.XYZ"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
name|getPrefix
argument_list|()
operator|+
literal|"FOO.X.Y"
argument_list|)
block|}
return|;
block|}
specifier|protected
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|super
operator|.
name|getSubject
argument_list|()
operator|+
literal|"."
return|;
block|}
block|}
end_class

end_unit

