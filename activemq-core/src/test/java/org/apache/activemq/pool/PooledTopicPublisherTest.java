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
name|pool
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ActiveMQConnectionFactory
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
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|PooledTopicPublisherTest
extends|extends
name|TestCase
block|{
specifier|private
name|TopicConnection
name|connection
decl_stmt|;
specifier|public
name|void
name|testPooledConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|topic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|PooledConnectionFactory
name|pcf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|pcf
operator|.
name|setConnectionFactory
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://test"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|TopicConnection
operator|)
name|pcf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|TopicSession
name|session
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|TopicPublisher
name|publisher
init|=
name|session
operator|.
name|createPublisher
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|publish
argument_list|(
name|session
operator|.
name|createMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

