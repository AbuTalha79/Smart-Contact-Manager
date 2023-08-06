package com.smart.smartcontactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.helper.Message;
import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.repoDao.ContactRepository;
import com.smart.smartcontactmanager.repoDao.UserRepository;

//after login this page will show (user_dashboard.html)
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bcCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


    //mothod for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model,Principal principal)
    {
        String userName = principal.getName();
        System.out.println("UserName" +userName);

        //get the user using username(email)
        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER: " +user); 
        model.addAttribute("user",user);
    }


    //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal)
    {
        
        model.addAttribute("title", "User Dashboard");
        return "normalUser/user_dashboard";
    }


    //open , add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model)
    {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "normalUser/add_contact_form";
    }

    //processing add contact form 
    @PostMapping("/process-contact")
    // @RequestMapping(value = "/process-contact", method = RequestMethod.POST)
    public String processContact(@ModelAttribute Contact contact,                      
                                 @RequestParam("profileImage") MultipartFile file,     
                                 Principal principal,
                                 HttpSession session) {                                

        try {
            // get user who logged in
            String name=  principal.getName();
        User user = this.userRepository.getUserByUserName(name);


        //processing and uploading file
        if(file.isEmpty())
        {
            //if file is empty then try our message
            System.out.println("File is empty");
            contact.setImage("contact.png");
        }
        else {
            //set the file to folder and update the name to contact
            contact.setImage(file.getOriginalFilename());

            File saveFile =new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Image is uploaded");
        }

        contact.setUser(user);
        user.getContacts().add(contact);
        
        this.userRepository.save(user);      // sava data in database

        System.out.println("Data" +contact); 

        System.out.println("Added to database...");

        //success message
        session.setAttribute("message", new Message("Your Contact is added !! Add next...", "success"));
        
        } catch (Exception e) {
            System.out.println("ERROR"+e.getMessage());
            e.printStackTrace();
            //error message
            session.setAttribute("message", new Message("Something went wrong...", "danger"));
        }
        
        return "normalUser/add_contact_form";
    }

    //show contact handler
    // per page = 5[n]
    //current page = 0 [page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page, Model m, Principal principal) {

        m.addAttribute("title", "View Conatct");

        //send list of contacts
        //fetch Contacts From saved contacts
        //this is also a method to fetch data by using principal over initialization
        // String userName=principal.getName();
        //  User user = this.userRepository.getUserByUserName(userName);
        // List<Contact> contacts = user.getContacts();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable =PageRequest.of(page, 10);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
        System.out.println("------------"+user.getId());
        
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        System.out.println("..................." +contacts);
        
        return "normalUser/show_contacts";
    }


    //showing particular contact details

    @GetMapping("/{cid}/contact")
    public String showContactDetail(@PathVariable("cid") Integer cid ,Model model, Principal principal){
        System.out.println(("CID" +cid));

        Optional<Contact> contactOptional= this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normalUser/contact_detail";
    }


    // delete handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session,Principal principal){

        System.out.println("CID" +cid);
        // Optional<Contact> contactOptional= this.contactRepository.findById(cid);
        Contact contact = this.contactRepository.findById(cid).get();

        //check if contact id and delete id match
        // do by yourself
        System.out.println("COntact " +contact.getCid());

        // contact.setUser(null);
        // this.contactRepository.delete(contact);

        User user = this.userRepository.getUserByUserName(principal.getName());

        user.getContacts().remove(contact);

        this.userRepository.save(user);

        System.out.println("DELETED");

        session.setAttribute("message", new Message("Contact delete successfully....", "success"));

        return "redirect:/user/show-contacts/0";
    }


    //open update form handler
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model m)
    {
        m.addAttribute("title", "Update Contact");

        Contact contact= this.contactRepository.findById(cid).get();

        m.addAttribute("contact", contact);

        return "normalUser/update_form";
    }


    // update contact handler
    @RequestMapping(value = "/process-update" , method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
    @RequestParam("profileImage") MultipartFile file,
    Model m, HttpSession session,Principal principal)
    {
        try {

            //get old contact details
            Contact oldcontactDetail = this.contactRepository.findById(contact.getCid()).get();




            //image
            if(!file.isEmpty())
            {
                //file work
                //rewrite

                //delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldcontactDetail.getImage());
                file1.delete();



                // update new photo

            File saveFile =new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            contact.setImage(file.getOriginalFilename());

            System.out.println("Image is uploaded");
                
            }else
            {
                contact.setImage(oldcontactDetail.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("message", new Message("Your Contact is updated", "success"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("CONTACT NAME " +contact.getName());
        System.out.println("CONTACT ID " +contact.getCid());

        return "redirect:/user/"+contact.getCid()+"/contact";
    }

    //your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model)
    {
        model.addAttribute("title", "Profile Page");
        return "normalUser/profile";
    }



    //open setting handler
    @GetMapping("/settings")
    public String openSettings()
    {
        return "normalUser/settings";
    }

    
    //change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
    @RequestParam("newPassword") String newPassword,Principal principal,
                    HttpSession session)
    {
        System.out.println("OLD PASSWORD " +oldPassword);
        System.out.println("NEW PASSWORD " +newPassword);
        

        //fetch user
        String userName = principal.getName();
        User currentUser =  this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if(this.bcCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
        {
            //change password
            currentUser.setPassword(this.bcCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Password Changed", "success"));
        }else{
            //error

            session.setAttribute("message", new Message("Please Enter Correct Old Password", "danger"));
            return "redirect:/user/settings";

        }

        return "redirect:/user/index";
    }
}
