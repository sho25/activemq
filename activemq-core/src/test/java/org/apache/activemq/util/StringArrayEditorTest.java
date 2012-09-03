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
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|StringArrayEditorTest
extends|extends
name|TestCase
block|{
specifier|private
name|StringArrayEditor
name|editor
init|=
operator|new
name|StringArrayEditor
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testConvertToStringArray
parameter_list|()
throws|throws
name|Exception
block|{
name|editor
operator|.
name|setAsText
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|editor
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setAsText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|editor
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setAsText
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|String
index|[]
name|array
init|=
operator|(
name|String
index|[]
operator|)
name|editor
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|array
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setAsText
argument_list|(
literal|"foo,bar"
argument_list|)
expr_stmt|;
name|array
operator|=
operator|(
name|String
index|[]
operator|)
name|editor
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|array
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|array
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setAsText
argument_list|(
literal|"foo,bar,baz"
argument_list|)
expr_stmt|;
name|array
operator|=
operator|(
name|String
index|[]
operator|)
name|editor
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|array
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|array
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"baz"
argument_list|,
name|array
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConvertToString
parameter_list|()
throws|throws
name|Exception
block|{
name|editor
operator|.
name|setValue
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|editor
operator|.
name|getAsText
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setValue
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|editor
operator|.
name|getAsText
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setValue
argument_list|(
operator|new
name|String
index|[]
block|{
literal|""
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|editor
operator|.
name|getAsText
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setValue
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|editor
operator|.
name|getAsText
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setValue
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo,bar"
argument_list|,
name|editor
operator|.
name|getAsText
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setValue
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"baz"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo,bar,baz"
argument_list|,
name|editor
operator|.
name|getAsText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

