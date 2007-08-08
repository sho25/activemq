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
name|filter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
comment|/**  *  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|BadDummyPolicyConfigTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BadDummyPolicyConfigTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|DummyPolicy
name|policy
init|=
operator|new
name|DummyPolicy
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testNoDestinationSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyPolicyEntry
name|entry
init|=
operator|new
name|DummyPolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setDescription
argument_list|(
literal|"cheese"
argument_list|)
expr_stmt|;
name|assertFailsToSetEntries
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNoValueSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyPolicyEntry
name|entry
init|=
operator|new
name|DummyPolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|"FOO.BAR"
argument_list|)
expr_stmt|;
name|assertFailsToSetEntries
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testValidEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyPolicyEntry
name|entry
init|=
operator|new
name|DummyPolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setDescription
argument_list|(
literal|"cheese"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|"FOO.BAR"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|assertFailsToSetEntries
parameter_list|(
name|DummyPolicyEntry
name|entry
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|entry
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Worked! Caught expected exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

