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
name|kahadb
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Very similar to the java.io.ByteArrayInputStream but this version is not  * thread safe.  */
end_comment

begin_class
specifier|public
class|class
name|ByteArrayInputStream
extends|extends
name|InputStream
block|{
name|byte
name|buffer
index|[]
decl_stmt|;
name|int
name|limit
decl_stmt|;
name|int
name|pos
decl_stmt|;
name|int
name|mark
decl_stmt|;
specifier|public
name|ByteArrayInputStream
parameter_list|(
name|byte
name|data
index|[]
parameter_list|)
block|{
name|this
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ByteArrayInputStream
parameter_list|(
name|ByteSequence
name|sequence
parameter_list|)
block|{
name|this
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ByteArrayInputStream
parameter_list|(
name|byte
name|data
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|mark
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|offset
operator|+
name|size
expr_stmt|;
block|}
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|<
name|limit
condition|)
block|{
return|return
name|buffer
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
specifier|public
name|int
name|read
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
name|limit
condition|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|limit
operator|-
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|<
name|limit
condition|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|limit
operator|-
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|pos
operator|+=
name|len
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
name|limit
operator|-
name|pos
return|;
block|}
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|markpos
parameter_list|)
block|{
name|mark
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|pos
operator|=
name|mark
expr_stmt|;
block|}
block|}
end_class

end_unit

