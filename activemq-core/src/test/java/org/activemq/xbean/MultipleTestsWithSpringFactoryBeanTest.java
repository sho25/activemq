begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|xbean
package|;
end_package

begin_import
import|import
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *   * @author Neil Clayton  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MultipleTestsWithSpringFactoryBeanTest
extends|extends
name|TestCase
block|{
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|BrokerService
name|service
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|test2
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### starting up the test case: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/activemq/xbean/spring2.xml"
argument_list|)
expr_stmt|;
name|service
operator|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"broker"
argument_list|)
expr_stmt|;
comment|// already started
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### started up the test case: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// stopped as part of the context
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### closed down the test case: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setBrokerURL
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

