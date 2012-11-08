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
name|console
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jasypt
operator|.
name|exceptions
operator|.
name|EncryptionOperationNotPossibleException
import|;
end_import

begin_class
specifier|public
class|class
name|DecryptCommand
extends|extends
name|EncryptCommand
block|{
specifier|protected
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Task Usage: Main decrypt --password<password> --input<input>"
block|,
literal|"Description: Decrypts given text."
block|,
literal|""
block|,
literal|"Encrypt Options:"
block|,
literal|"    --password<password>      Password to be used by the encryptor."
block|,
literal|"    --input<input>            Text to be encrypted."
block|,
literal|"    --version                  Display the version information."
block|,
literal|"    -h,-?,--help               Display the stop broker help information."
block|,
literal|""
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"decrypt"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOneLineDescription
parameter_list|()
block|{
return|return
literal|"Decrypts given text"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|password
operator|==
literal|null
operator|||
name|input
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"input and password parameters are mandatory"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|encryptor
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|print
argument_list|(
literal|"Decrypted text: "
operator|+
name|encryptor
operator|.
name|decrypt
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptionOperationNotPossibleException
name|e
parameter_list|)
block|{
name|context
operator|.
name|print
argument_list|(
literal|"ERROR: Text cannot be decrypted, check your input and password and try again!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

