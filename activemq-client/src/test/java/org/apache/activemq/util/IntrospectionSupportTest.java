begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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

begin_class
specifier|public
class|class
name|IntrospectionSupportTest
block|{
specifier|private
class|class
name|DummyClass
block|{
specifier|private
name|boolean
name|trace
decl_stmt|;
name|DummyClass
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPropertyPrimitiveWithWrapperValue
parameter_list|()
block|{
comment|// Wrapper value
name|Boolean
name|value
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|DummyClass
name|dummyClass
init|=
operator|new
name|DummyClass
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|dummyClass
operator|.
name|setTrace
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// dummy field expects a primitive
name|IntrospectionSupport
operator|.
name|setProperty
argument_list|(
name|dummyClass
argument_list|,
literal|"trace"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dummyClass
operator|.
name|isTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

