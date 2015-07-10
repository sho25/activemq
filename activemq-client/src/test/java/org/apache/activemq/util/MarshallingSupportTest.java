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
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|MarshallingSupportTest
block|{
comment|/**      * Test method for      * {@link org.apache.activemq.util.MarshallingSupport#propertiesToString(java.util.Properties)}.      *      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testPropertiesToString
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"key"
operator|+
name|i
decl_stmt|;
name|String
name|value
init|=
literal|"value"
operator|+
name|i
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|String
name|str
init|=
name|MarshallingSupport
operator|.
name|propertiesToString
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|Properties
name|props2
init|=
name|MarshallingSupport
operator|.
name|stringToProperties
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|props
argument_list|,
name|props2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

