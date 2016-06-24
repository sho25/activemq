begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_class
specifier|public
class|class
name|AnonymousSimplePojoParent
implements|implements
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
name|SimplePojo
name|payload
decl_stmt|;
specifier|public
name|AnonymousSimplePojoParent
parameter_list|(
name|Object
name|simplePojoPayload
parameter_list|)
block|{
comment|// Create an ANONYMOUS simple payload, itself serializable, like we
comment|// have to be since the object references us and is used
comment|// during the serialization.
name|payload
operator|=
operator|new
name|SimplePojo
argument_list|(
name|simplePojoPayload
argument_list|)
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
block|}
expr_stmt|;
block|}
specifier|public
name|SimplePojo
name|getPayload
parameter_list|()
block|{
return|return
name|payload
return|;
block|}
block|}
end_class

end_unit

