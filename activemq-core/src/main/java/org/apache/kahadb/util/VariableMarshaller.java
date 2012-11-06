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

begin_comment
comment|/**  * Convenience base class for Marshaller implementations which do not deepCopy and  * which use variable size encodings.  *   * @author chirino  * @param<T>  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|VariableMarshaller
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Marshaller
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|int
name|getFixedSize
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|boolean
name|isDeepCopySupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|T
name|deepCopy
parameter_list|(
name|T
name|source
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

