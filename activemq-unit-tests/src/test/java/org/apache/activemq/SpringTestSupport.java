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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_comment
comment|/**  * A useful base class for spring based unit test cases  *  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SpringTestSupport
extends|extends
name|TestCase
block|{
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|=
name|createApplicationContext
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|AbstractApplicationContext
name|createApplicationContext
parameter_list|()
function_decl|;
empty_stmt|;
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
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Object
name|getBean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Object
name|bean
init|=
name|context
operator|.
name|getBean
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|bean
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Should have found bean named '"
operator|+
name|name
operator|+
literal|"' in the Spring ApplicationContext"
argument_list|)
expr_stmt|;
block|}
return|return
name|bean
return|;
block|}
specifier|protected
name|void
name|assertSetEquals
parameter_list|(
name|String
name|description
parameter_list|,
name|Object
index|[]
name|expected
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|actual
parameter_list|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|expectedSet
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|expectedSet
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|description
argument_list|,
name|expectedSet
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

