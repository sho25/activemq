begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_comment
comment|/**  * Helper utility that can be used to set the properties on any object using  * command line arguments.  *   * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CommandLineSupport
block|{
specifier|private
name|CommandLineSupport
parameter_list|()
block|{     }
comment|/**      * Sets the properties of an object given the command line args.      *       * if args contains: --ack-mode=AUTO --url=tcp://localhost:61616 --persistent       *       * then it will try to call the following setters on the target object.      *       * target.setAckMode("AUTO");      * target.setURL(new URI("tcp://localhost:61616") );      * target.setPersistent(true);      *       * Notice the the proper conversion for the argument is determined by examining the       * setter arguement type.        *       * @param target the object that will have it's properties set      * @param args the commline options      * @return any arguments that are not valid options for the target      */
specifier|public
specifier|static
name|String
index|[]
name|setOptions
parameter_list|(
name|Object
name|target
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
comment|// --options without a specified value are considered boolean
comment|// flags that are enabled.
name|String
name|value
init|=
literal|"true"
decl_stmt|;
name|String
name|name
init|=
name|args
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// if --option=value case
name|int
name|p
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
literal|0
condition|)
block|{
name|value
operator|=
name|name
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
comment|// name not set, then it's an unrecognized option
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|propName
init|=
name|convertOptionToPropertyName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|IntrospectionSupport
operator|.
name|setProperty
argument_list|(
name|target
argument_list|,
name|propName
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
block|}
name|String
name|r
index|[]
init|=
operator|new
name|String
index|[
name|rc
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|rc
operator|.
name|toArray
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/**      * converts strings like: test-enabled to testEnabled      *       * @param name      * @return      */
specifier|private
specifier|static
name|String
name|convertOptionToPropertyName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|rc
init|=
literal|""
decl_stmt|;
comment|// Look for '-' and strip and then convert the subsequent char to
comment|// uppercase
name|int
name|p
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
while|while
condition|(
name|p
operator|>
literal|0
condition|)
block|{
comment|// strip
name|rc
operator|+=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// can I convert the next char to upper?
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|rc
operator|+=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|p
operator|=
name|name
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

