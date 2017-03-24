<?php


   $con=mysqli_connect("localhost","root","","youspot");

	mysqli_query($con){
		
			 $date = $_POST['date'];
			 $heure = $_POST['heure'];
	
			if($date==''  || $heure==''){
				
				echo "merci de saisir le créneau";
				
			}
	 
			else{
					$verif="SELECT * FROM Creneau WHERE dateCreneau='$date' AND heure='$heure'";
					
					 $check = mysqli_fetch_array(mysqli_query($con,$verif));
					 
					 if(isset($check)){
						 echo "séance déjà reservé";
					 }
					else{
						
							$sql="INSERT INTO Creneau (dateCreneau, heure, disponible) VALUES ('$date', '$heure','true')";
							
							if(mysqli_query($con,$sql)){
								echo "reservation terminé avec succès";
							}
							else {
								echo "erreur de transaction";
							}
								
					}
	
			}
	 mysqli_close($con);	
		
		
	}
	
	



  
?>