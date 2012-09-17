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
name|java
operator|.
name|beans
operator|.
name|PropertyEditorSupport
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Converts string values like "20 Mb", "1024kb", and "1g" to long values in  * bytes.  */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|MemoryPropertyEditor
extends|extends
name|PropertyEditorSupport
block|{
specifier|public
name|void
name|setAsText
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|p
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*k(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
expr_stmt|;
name|m
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|p
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*m(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
expr_stmt|;
name|m
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|p
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*g(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
expr_stmt|;
name|m
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could convert not to a memory size: "
operator|+
name|text
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getAsText
parameter_list|()
block|{
name|Long
name|value
init|=
operator|(
name|Long
operator|)
name|getValue
argument_list|()
decl_stmt|;
return|return
name|value
operator|!=
literal|null
condition|?
name|value
operator|.
name|toString
argument_list|()
else|:
literal|""
return|;
block|}
block|}
end_class

end_unit

