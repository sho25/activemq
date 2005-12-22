begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionCleanupTest
extends|extends
name|TestCase
block|{
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see junit.framework.TestCase#tearDown()      */
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
block|}
comment|/**      * @throws JMSException      */
specifier|public
name|void
name|testChangeClientID
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|Session
name|session
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
try|try
block|{
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
comment|//fail("Should have received JMSException");
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
name|connection
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
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
expr_stmt|;
try|try
block|{
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
comment|//fail("Should have received JMSException");
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
block|}
block|}
end_class

end_unit

