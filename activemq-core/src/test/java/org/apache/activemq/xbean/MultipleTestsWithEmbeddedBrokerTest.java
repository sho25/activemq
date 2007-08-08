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
name|xbean
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
name|EmbeddedBrokerTestSupport
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

begin_comment
comment|/**  *   * @author Neil Clayton  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MultipleTestsWithEmbeddedBrokerTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|protected
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
name|LOG
operator|.
name|info
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
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"### closed down the test case: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

