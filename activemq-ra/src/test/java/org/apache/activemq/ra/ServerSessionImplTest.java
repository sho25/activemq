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
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpoint
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
name|work
operator|.
name|WorkManager
import|;
end_import

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
name|ActiveMQConnection
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
name|ActiveMQSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Expectations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Mockery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|integration
operator|.
name|junit4
operator|.
name|JMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|lib
operator|.
name|legacy
operator|.
name|ClassImposteriser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|JMock
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ServerSessionImplTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"vm://localhost"
decl_stmt|;
specifier|private
name|ServerSessionImpl
name|serverSession
decl_stmt|;
specifier|private
name|ServerSessionPoolImpl
name|pool
decl_stmt|;
specifier|private
name|WorkManager
name|workManager
decl_stmt|;
specifier|private
name|MessageEndpoint
name|messageEndpoint
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|con
decl_stmt|;
specifier|private
name|ActiveMQSession
name|session
decl_stmt|;
specifier|private
name|Mockery
name|context
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|Mockery
argument_list|()
block|{
block|{
name|setImposteriser
parameter_list|(
name|ClassImposteriser
operator|.
name|INSTANCE
parameter_list|)
constructor_decl|;
block|}
block|}
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
argument_list|(
name|BROKER_URL
argument_list|)
decl_stmt|;
name|con
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
operator|(
name|ActiveMQSession
operator|)
name|con
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|pool
operator|=
name|context
operator|.
name|mock
argument_list|(
name|ServerSessionPoolImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|workManager
operator|=
name|context
operator|.
name|mock
argument_list|(
name|WorkManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|serverSession
operator|=
operator|new
name|ServerSessionImpl
argument_list|(
operator|(
name|ServerSessionPoolImpl
operator|)
name|pool
argument_list|,
name|session
argument_list|,
operator|(
name|WorkManager
operator|)
name|workManager
argument_list|,
name|messageEndpoint
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRunDetectsStoppedSession
parameter_list|()
throws|throws
name|Exception
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|context
operator|.
name|checking
argument_list|(
operator|new
name|Expectations
argument_list|()
block|{
block|{
name|oneOf
argument_list|(
name|pool
argument_list|)
operator|.
name|removeFromPool
argument_list|(
name|with
argument_list|(
name|same
argument_list|(
name|serverSession
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|serverSession
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

